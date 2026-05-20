// Location: app/src/main/java/com/sscprephub/app/presentation/ScreenDestination.kt
package com.sscprephub.app.presentation

object ScreenDestination {
    const val DASHBOARD = "dashboard"
    const val GLOBAL_BOOKMARKS = "global_bookmarks"
    
    const val SUBJECT_DETAIL = "subject_detail/{subjectId}/{subjectName}"
    fun createSubjectDetailRoute(id: Int, name: String) = "subject_detail/$id/$name"

    const val TOPIC_DETAIL = "topic_detail/{topicId}/{topicName}"
    fun createTopicDetailRoute(id: Int, name: String) = "topic_detail/$id/$name"

    const val PYQ_PAPER_LIST = "pyq_paper_list/{topicId}/{topicName}"
    fun createPyqPaperListRoute(id: Int, name: String) = "pyq_paper_list/$id/$name"

    const val VIDEO_PLAYLIST = "video_playlist/{topicId}/{topicName}"
    fun createVideoPlaylistRoute(id: Int, name: String) = "video_playlist/$id/$name"
}
