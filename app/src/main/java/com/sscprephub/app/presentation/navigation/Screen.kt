// Location: app/src/main/java/com/sscprephub/app/presentation/navigation/Screen.kt
package com.sscprephub.app.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    // Core Bottom Navigation Root Destinations
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Bookmarks : Screen("bookmarks", "Bookmarks", Icons.Default.Bookmark)
    object Progress : Screen("progress", "Progress", Icons.Default.Assignment)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)

    // Secondary Detail Screens
    object SubjectDetail : Screen("subject_detail/{subjectId}/{subjectName}", "Subject Details") {
        fun createRoute(subjectId: Int, subjectName: String) = "subject_detail/$subjectId/$subjectName"
    }
    
    object TopicDetail : Screen("topic_detail/{topicId}/{topicName}/{subjectName}", "Topic Details") {
        fun createRoute(topicId: Int, topicName: String, subjectName: String) = "topic_detail/$topicId/$topicName/$subjectName"
    }
}
