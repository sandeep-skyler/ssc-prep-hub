// Location: app/src/main/java/com/sscprephub/app/data/local/database/AppDatabase.kt
package com.sscprephub.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sscprephub.app.data.local.dao.PrepDao
import com.sscprephub.app.data.local.entity.PYTPaperEntity
import com.sscprephub.app.data.local.entity.SubjectEntity
import com.sscprephub.app.data.local.entity.TopicEntity
import com.sscprephub.app.data.local.entity.YTPlaylistEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

@Database(
    entities = [SubjectEntity::class, TopicEntity::class, PYTPaperEntity::class, YTPlaylistEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun prepDao(): PrepDao
}

class AppDatabaseCallback(
    private val databaseProvider: Provider<AppDatabase>,
    private val applicationScope: CoroutineScope
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // Safely hand execution off to an application-scoped background thread
        applicationScope.launch(Dispatchers.IO) {
            seedDefaultSSCData()
        }
    }

    private suspend fun seedDefaultSSCData() {
        val dao = databaseProvider.get().prepDao()

        // 1. Seed the 4 core mandatory subjects
        val subjects = listOf(
            SubjectEntity(id = 1, name = "Reasoning", icon = "psychology"),
            SubjectEntity(id = 2, name = "English", icon = "translate"),
            SubjectEntity(id = 3, name = "Maths", icon = "functions"),
            SubjectEntity(id = 4, name = "GK", icon = "public")
        )
        dao.insertSubjects(subjects)

        // 2. Seed default bonus structural topics
        val defaultTopics = listOf(
            // Reasoning Topics
            TopicEntity(subjectId = 1, name = "Coding-Decoding"),
            TopicEntity(subjectId = 1, name = "Blood Relation"),
            TopicEntity(subjectId = 1, name = "Syllogism"),
            
            // English Topics
            TopicEntity(subjectId = 2, name = "Grammar"),
            TopicEntity(subjectId = 2, name = "Vocabulary"),
            TopicEntity(subjectId = 2, name = "Reading Comprehension"),
            
            // Maths Topics
            TopicEntity(subjectId = 3, name = "Percentage"),
            TopicEntity(subjectId = 3, name = "Profit & Loss"),
            TopicEntity(subjectId = 3, name = "Algebra"),
            
            // GK Topics
            TopicEntity(subjectId = 4, name = "History"),
            TopicEntity(subjectId = 4, name = "Polity"),
            TopicEntity(subjectId = 4, name = "Geography")
        )

        // Insert each topic cleanly through the DAO interface
        defaultTopics.forEach { topic -> dao.insertTopic(topic) }
    }
}
