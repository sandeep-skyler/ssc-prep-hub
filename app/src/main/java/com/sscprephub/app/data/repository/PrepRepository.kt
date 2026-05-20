// Location: app/src/main/java/com/sscprephub/app/data/repository/PrepRepository.kt
package com.sscprephub.app.data.repository

import com.sscprephub.app.data.local.dao.PrepDao
import com.sscprephub.app.data.local.entity.PYTPaperEntity
import com.sscprephub.app.data.local.entity.SubjectEntity
import com.sscprephub.app.data.local.entity.TopicEntity
import com.sscprephub.app.data.local.entity.YTPlaylistEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrepRepository @Inject constructor(
    private val prepDao: PrepDao
) {

    // Subjects
    val allSubjects: Flow<List<SubjectEntity>> = prepDao.getAllSubjects()
    
    suspend fun getSubjectById(id: Int): SubjectEntity? = prepDao.getSubjectById(id)

    // Topics
    fun getTopicsBySubject(subjectId: Int): Flow<List<TopicEntity>> = 
        prepDao.getTopicsBySubject(subjectId)

    fun searchTopics(subjectId: Int, query: String): Flow<List<TopicEntity>> = 
        prepDao.searchTopics(subjectId, query)

    suspend fun insertTopic(topic: TopicEntity) = prepDao.insertTopic(topic)

    suspend fun updateTopic(topic: TopicEntity) = prepDao.updateTopic(topic)

    suspend fun deleteTopic(topic: TopicEntity) = prepDao.deleteTopic(topic)

    suspend fun updateTopicCompletion(topicId: Int, completed: Boolean) = 
        prepDao.updateTopicCompletion(topicId, completed)

    suspend fun updateTopicBookmark(topicId: Int, bookmarked: Boolean) = 
        prepDao.updateTopicBookmark(topicId, bookmarked)

    // Previous Year Papers (PYT)
    fun getPYTPapersByTopic(topicId: Int): Flow<List<PYTPaperEntity>> = 
        prepDao.getPYTPapersByTopic(topicId)

    fun getPYTPapersByYear(topicId: Int, year: Int): Flow<List<PYTPaperEntity>> = 
        prepDao.getPYTPapersByYear(topicId, year)

    suspend fun insertPYTPaper(paper: PYTPaperEntity) = prepDao.insertPYTPaper(paper)

    suspend fun updatePYTPaper(paper: PYTPaperEntity) = prepDao.updatePYTPaper(paper)

    suspend fun deletePYTPaper(paper: PYTPaperEntity) = prepDao.deletePYTPaper(paper)

    suspend fun updatePYTBookmark(id: Int, bookmarked: Boolean) = 
        prepDao.updatePYTBookmark(id, bookmarked)

    // YouTube Playlists
    fun getPlaylistsByTopic(topicId: Int): Flow<List<YTPlaylistEntity>> = 
        prepDao.getPlaylistsByTopic(topicId)

    suspend fun insertPlaylist(playlist: YTPlaylistEntity) = prepDao.insertPlaylist(playlist)

    suspend fun updatePlaylist(playlist: YTPlaylistEntity) = prepDao.updatePlaylist(playlist)

    suspend fun deletePlaylist(playlist: YTPlaylistEntity) = prepDao.deletePlaylist(playlist)

    suspend fun updatePlaylistBookmark(id: Int, bookmarked: Boolean) = 
        prepDao.updatePlaylistBookmark(id, bookmarked)

    // Bookmarks Overview
    val bookmarkedTopics: Flow<List<TopicEntity>> = prepDao.getBookmarkedTopics()
    val bookmarkedPYTPapers: Flow<List<PYTPaperEntity>> = prepDao.getBookmarkedPYTPapers()
    val bookmarkedPlaylists: Flow<List<YTPlaylistEntity>> = prepDao.getBookmarkedPlaylists()

    // Dashboard Statistics & Progress tracking streams
    val totalTopicsCount: Flow<Int> = prepDao.getTotalTopicsCount()
    val completedTopicsCount: Flow<Int> = prepDao.getCompletedTopicsCount()
    val totalPYTPapersCount: Flow<Int> = prepDao.getTotalPYTPapersCount()
    val totalPlaylistsCount: Flow<Int> = prepDao.getTotalPlaylistsCount()
    val recentlyUpdatedTopics: Flow<List<TopicEntity>> = prepDao.getRecentlyUpdatedTopics()

    fun getTopicCountBySubject(subjectId: Int): Flow<Int> = 
        prepDao.getTopicCountBySubject(subjectId)

    fun getCompletedTopicCountBySubject(subjectId: Int): Flow<Int> = 
        prepDao.getCompletedTopicCountBySubject(subjectId)
}
