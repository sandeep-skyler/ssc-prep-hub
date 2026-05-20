// Location: app/src/main/java/com/sscprephub/app/presentation/ScreenDestination.kt
package com.sscprephub.app.presentation

import kotlinx.serialization.Serializable

sealed interface ScreenDestination {
    
    @Serializable
    data object Dashboard : ScreenDestination

    @Serializable
    data class SubjectDetail(
        val subjectId: Int,
        val subjectName: String
    ) : ScreenDestination

    @Serializable
    data class TopicDetail(
        val topicId: Int,
        val topicName: String
    ) : ScreenDestination

    @Serializable
    data class PYQPaperList(
        val topicId: Int,
        val topicName: String
    ) : ScreenDestination

    @Serializable
    data class VideoPlaylist(
        val topicId: Int,
        val topicName: String
    ) : ScreenDestination

    @Serializable
    data object GlobalBookmarks : ScreenDestination
}
