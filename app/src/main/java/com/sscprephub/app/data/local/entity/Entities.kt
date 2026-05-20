// Location: app/src/main/java/com/sscprephub/app/data/local/entity/Entities.kt
package com.sscprephub.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val icon: String, // String identifier for selecting Material vector icons dynamically
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "topics",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["subjectId"])]
)
data class TopicEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectId: Int,
    val name: String,
    val isCompleted: Boolean = false,
    val isBookmarked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "pyt_papers",
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["topicId"])]
)
data class PYTPaperEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topicId: Int,
    val title: String,
    val year: Int,
    val link: String,
    val description: String,
    val isBookmarked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "yt_playlists",
    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["topicId"])]
)
data class YTPlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topicId: Int,
    val title: String,
    val url: String,
    val instructor: String,
    val notes: String,
    val isBookmarked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
