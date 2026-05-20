// Location: app/src/main/java/com/sscprephub/app/presentation/viewmodel/PrepViewModel.kt
package com.sscprephub.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sscprephub.app.data.local.entity.PYTPaperEntity
import com.sscprephub.app.data.local.entity.SubjectEntity
import com.sscprephub.app.data.local.entity.TopicEntity
import com.sscprephub.app.data.local.entity.YTPlaylistEntity
import com.sscprephub.app.data.repository.PrepRepository
import com.sscprephub.app.domain.model.DashboardStats
import com.sscprephub.app.domain.model.SubjectWithStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PrepViewModel @Inject constructor(
    private val repository: PrepRepository
) : ViewModel() {

    // ------------------------------------------------------------------------
    // DASHBOARD & OVERALL STATS STATE
    // ------------------------------------------------------------------------
    val dashboardStats: StateFlow<DashboardStats> = combine(
        repository.totalTopicsCount,
        repository.completedTopicsCount,
        repository.totalPYTPapersCount,
        repository.totalPlaylistsCount
    ) { total, completed, pytCount, ytCount ->
        val progress = if (total > 0) (completed * 100) / total else 0
        DashboardStats(total, completed, pytCount, ytCount, progress)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardStats())

    val recentlyUpdatedTopics: StateFlow<List<TopicEntity>> = repository.recentlyUpdatedTopics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined Subject flow with individual live topic counts and progress tracking
    val subjectsWithStats: StateFlow<List<SubjectWithStats>> = repository.allSubjects
        .flatMapLatest { subjectList ->
            if (subjectList.isEmpty()) return@flatMapLatest flowOf(emptyList())
            
            val flows = subjectList.map { subject ->
                combine(
                    repository.getTopicCountBySubject(subject.id),
                    repository.getCompletedTopicCountBySubject(subject.id)
                ) { total, completed ->
                    val pct = if (total > 0) (completed * 100) / total else 0
                    SubjectWithStats(subject, total, completed, pct)
                }
            }
            combine(flows) { it.toList() }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ------------------------------------------------------------------------
    // SUBJECT DETAILS STATE MANAGEMENT
    // ------------------------------------------------------------------------
    private val _currentSubjectId = MutableStateFlow<Int?>(null)
    private val _topicSearchQuery = MutableStateFlow("")
    val topicSearchQuery: StateFlow<String> = _topicSearchQuery.asStateFlow()

    val currentTopicsList: StateFlow<List<TopicEntity>> = combine(
        _currentSubjectId,
        _topicSearchQuery
    ) { subjectId, query ->
        Pair(subjectId, query)
    }.flatMapLatest { (subjectId, query) ->
        if (subjectId == null) return@flatMapLatest flowOf(emptyList())
        if (query.isBlank()) {
            repository.getTopicsBySubject(subjectId)
        } else {
            repository.searchTopics(subjectId, query)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectSubject(subjectId: Int) {
        _currentSubjectId.value = subjectId
        _topicSearchQuery.value = "" // Clean query when jumping between subjects
    }

    fun updateTopicSearch(query: String) {
        _topicSearchQuery.value = query
    }

    // ------------------------------------------------------------------------
    // TOPIC DETAIL STATE MANAGEMENT (PYT PAPERS + YOUTUBE PLAYLISTS)
    // ------------------------------------------------------------------------
    private val _currentTopicId = MutableStateFlow<Int?>(null)
    private val _pytYearFilter = MutableStateFlow<Int?>(null)
    val pytYearFilter: StateFlow<Int?> = _pytYearFilter.asStateFlow()

    val currentPYTPapers: StateFlow<List<PYTPaperEntity>> = combine(
        _currentTopicId,
        _pytYearFilter
    ) { topicId, year ->
        Pair(topicId, year)
    }.flatMapLatest { (topicId, year) ->
        if (topicId == null) return@flatMapLatest flowOf(emptyList())
        if (year == null) {
            repository.getPYTPapersByTopic(topicId)
        } else {
            repository.getPYTPapersByYear(topicId, year)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentYTPlaylists: StateFlow<List<YTPlaylistEntity>> = _currentTopicId
        .flatMapLatest { topicId ->
            if (topicId == null) flowOf(emptyList()) else repository.getPlaylistsByTopic(topicId)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTopic(topicId: Int) {
        _currentTopicId.value = topicId
        _pytYearFilter.value = null // Clear year filter automatically on load
    }

    fun setYearFilter(year: Int?) {
        _pytYearFilter.value = year
    }

    // ------------------------------------------------------------------------
    // BOOKMARKS SCREENS GLOBAL STATES
    // ------------------------------------------------------------------------
    val bookmarkedTopics: StateFlow<List<TopicEntity>> = repository.bookmarkedTopics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val bookmarkedPYTPapers: StateFlow<List<PYTPaperEntity>> = repository.bookmarkedPYTPapers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val bookmarkedPlaylists: StateFlow<List<YTPlaylistEntity>> = repository.bookmarkedPlaylists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ------------------------------------------------------------------------
    // COROUTINE EXECUTORS (DATABASE WRITE OPERATIONS)
    // ------------------------------------------------------------------------
    fun insertTopic(name: String, subjectId: Int) = viewModelScope.launch {
        repository.insertTopic(TopicEntity(name = name, subjectId = subjectId))
    }

    fun updateTopic(topic: TopicEntity) = viewModelScope.launch {
        repository.updateTopic(topic.copy(updatedAt = System.currentTimeMillis()))
    }

    fun deleteTopic(topic: TopicEntity) = viewModelScope.launch {
        repository.deleteTopic(topic)
    }

    fun toggleTopicCompletion(topicId: Int, isCompleted: Boolean) = viewModelScope.launch {
        repository.updateTopicCompletion(topicId, isCompleted)
    }

    fun toggleTopicBookmark(topicId: Int, isBookmarked: Boolean) = viewModelScope.launch {
        repository.updateTopicBookmark(topicId, isBookmarked)
    }

    fun insertPYTPaper(topicId: Int, title: String, year: Int, link: String, desc: String) = viewModelScope.launch {
        repository.insertPYTPaper(PYTPaperEntity(topicId = topicId, title = title, year = year, link = link, description = desc))
    }

    fun updatePYTPaper(paper: PYTPaperEntity) = viewModelScope.launch {
        repository.updatePYTPaper(paper.copy(updatedAt = System.currentTimeMillis()))
    }

    fun deletePYTPaper(paper: PYTPaperEntity) = viewModelScope.launch {
        repository.deletePYTPaper(paper)
    }

    fun togglePYTBookmark(id: Int, isBookmarked: Boolean) = viewModelScope.launch {
        repository.updatePYTBookmark(id, isBookmarked)
    }

    fun insertPlaylist(topicId: Int, title: String, url: String, instructor: String, notes: String) = viewModelScope.launch {
        repository.insertPlaylist(YTPlaylistEntity(topicId = topicId, title = title, url = url, instructor = instructor, notes = notes))
    }

    fun updatePlaylist(playlist: YTPlaylistEntity) = viewModelScope.launch {
        repository.updatePlaylist(playlist.copy(updatedAt = System.currentTimeMillis()))
    }

    fun deletePlaylist(playlist: YTPlaylistEntity) = viewModelScope.launch {
        repository.deletePlaylist(playlist)
    }

    fun togglePlaylistBookmark(id: Int, isBookmarked: Boolean) = viewModelScope.launch {
        repository.updatePlaylistBookmark(id, isBookmarked)
    }
}
