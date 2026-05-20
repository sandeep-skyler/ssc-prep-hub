// Location: app/src/main/java/com/sscprephub/app/data/local/dao/PrepDao.kt
package com.sscprephub.app.data.local.dao

import androidx.room.*
import com.sscprephub.app.data.local.entity.PYTPaperEntity
import com.sscprephub.app.data.local.entity.SubjectEntity
import com.sscprephub.app.data.local.entity.TopicEntity
import com.sscprephub.app.data.local.entity.YTPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PrepDao {

    // ========================================================================
    // SUBJECTS
    // ========================================================================
    @Query("SELECT * FROM subjects ORDER BY id ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    suspend fun getSubjectById(id: Int): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectEntity>)


    // ========================================================================
    // TOPICS
    // ========================================================================
    @Query("SELECT * FROM topics WHERE subjectId = :subjectId ORDER BY name ASC")
    fun getTopicsBySubject(subjectId: Int): Flow<List<TopicEntity>>

    @Query("""
        SELECT * FROM topics 
        WHERE subjectId = :subjectId AND name LIKE '%' || :query || '%' 
        ORDER BY name ASC
    """)
    fun searchTopics(subjectId: Int, query: String): Flow<List<TopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: TopicEntity)

    @Update
    suspend fun updateTopic(topic: TopicEntity)

    @Delete
    suspend fun deleteTopic(topic: TopicEntity)

    @Query("UPDATE topics SET isCompleted = :completed, updatedAt = :timestamp WHERE id = :topicId")
    suspend fun updateTopicCompletion(topicId: Int, completed: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE topics SET isBookmarked = :bookmarked, updatedAt = :timestamp WHERE id = :topicId")
    suspend fun updateTopicBookmark(topicId: Int, bookmarked: Boolean, timestamp: Long = System.currentTimeMillis())


    // ========================================================================
    // PREVIOUS YEAR PAPERS (PYT)
    // ========================================================================
    @Query("SELECT * FROM pyt_papers WHERE topicId = :topicId ORDER BY year DESC, title ASC")
    fun getPYTPapersByTopic(topicId: Int): Flow<List<PYTPaperEntity>>

    @Query("SELECT * FROM pyt_papers WHERE topicId = :topicId AND year = :year ORDER BY title ASC")
    fun getPYTPapersByYear(topicId: Int, year: Int): Flow<List<PYTPaperEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPYTPaper(paper: PYTPaperEntity)

    @Update
    suspend fun updatePYTPaper(paper: PYTPaperEntity)

    @Delete
    suspend fun deletePYTPaper(paper: PYTPaperEntity)

    @Query("UPDATE pyt_papers SET isBookmarked = :bookmarked, updatedAt = :timestamp WHERE id = :id")
    suspend fun updatePYTBookmark(id: Int, bookmarked: Boolean, timestamp: Long = System.currentTimeMillis())


    // ========================================================================
    // YOUTUBE PLAYLISTS
    // ========================================================================
    @Query("SELECT * FROM yt_playlists WHERE topicId = :topicId ORDER BY title ASC")
    fun getPlaylistsByTopic(topicId: Int): Flow<List<YTPlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: YTPlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: YTPlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: YTPlaylistEntity)

    @Query("UPDATE yt_playlists SET isBookmarked = :bookmarked, updatedAt = :timestamp WHERE id = :id")
    suspend fun updatePlaylistBookmark(id: Int, bookmarked: Boolean, timestamp: Long = System.currentTimeMillis())


    // ========================================================================
    // BOOKMARKS OVERVIEW
    // ========================================================================
    @Query("SELECT * FROM topics WHERE isBookmarked = 1 ORDER BY updatedAt DESC")
    fun getBookmarkedTopics(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM pyt_papers WHERE isBookmarked = 1 ORDER BY updatedAt DESC")
    fun getBookmarkedPYTPapers(): Flow<List<PYTPaperEntity>>

    @Query("SELECT * FROM yt_playlists WHERE isBookmarked = 1 ORDER BY updatedAt DESC")
    fun getBookmarkedPlaylists(): Flow<List<YTPlaylistEntity>>


    // ========================================================================
    // LIVE STATS & PROGRESS TRACKING
    // ========================================================================
    @Query("SELECT COUNT(*) FROM topics")
    fun getTotalTopicsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM topics WHERE isCompleted = 1")
    fun getCompletedTopicsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM pyt_papers")
    fun getTotalPYTPapersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM yt_playlists")
    fun getTotalPlaylistsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM topics WHERE subjectId = :subjectId")
    fun getTopicCountBySubject(subjectId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM topics WHERE subjectId = :subjectId AND isCompleted = 1")
    fun getCompletedTopicCountBySubject(subjectId: Int): Flow<Int>

    @Query("SELECT * FROM topics ORDER BY updatedAt DESC LIMIT 5")
    fun getRecentlyUpdatedTopics(): Flow<List<TopicEntity>>
}
