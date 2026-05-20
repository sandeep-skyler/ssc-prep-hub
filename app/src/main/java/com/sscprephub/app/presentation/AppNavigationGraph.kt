// Location: app/src/main/java/com/sscprephub/app/presentation/AppNavigationGraph.kt
package com.sscprephub.app.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.sscprephub.app.presentation.screen.*
import com.sscprephub.app.presentation.viewmodel.PrepViewModel

@Composable
fun AppNavigationGraph(
    navController: NavHostController,
    viewModel: PrepViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ScreenDestination.Dashboard,
        modifier = modifier
    ) {
        // 1. MAIN METRICS DASHBOARD
        composable<ScreenDestination.Dashboard> {
            DashboardScreen(
                viewModel = viewModel,
                onSubjectClick = { id, name ->
                    navController.navigate(ScreenDestination.SubjectDetail(subjectId = id, subjectName = name))
                },
                onBookmarksActionClick = {
                    navController.navigate(ScreenDestination.GlobalBookmarks)
                }
            )
        }

        // 2. SUBJECT DETAILS TRACKER
        composable<ScreenDestination.SubjectDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<ScreenDestination.SubjectDetail>()
            SubjectDetailScreen(
                subjectId = args.subjectId,
                subjectName = args.subjectName,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onTopicClick = { id, name ->
                    navController.navigate(ScreenDestination.TopicDetail(topicId = id, topicName = name))
                },
                onManagePyqClick = {
                    navController.navigate(ScreenDestination.PYQPaperList(topicId = args.subjectId, topicName = args.subjectName))
                },
                onManagePlaylistsClick = {
                    navController.navigate(ScreenDestination.VideoPlaylist(topicId = args.subjectId, topicName = args.subjectName))
                }
            )
        }

        // 3. TOPIC NOTEBOOK FOCUS WORKSPACE
        composable<ScreenDestination.TopicDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<ScreenDestination.TopicDetail>()
            TopicDetailScreen(
                topicId = args.topicId,
                topicName = args.topicName,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 4. PREVIOUS YEAR PAPERS EXPLORER
        composable<ScreenDestination.PYQPaperList> { backStackEntry ->
            val args = backStackEntry.toRoute<ScreenDestination.PYQPaperList>()
            PYQPaperListScreen(
                topicId = args.topicId,
                topicName = args.topicName,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 5. YOUTUBE VIDEO PLAYLIST TRACKER
        composable<ScreenDestination.VideoPlaylist> { backStackEntry ->
            val args = backStackEntry.toRoute<ScreenDestination.VideoPlaylist>()
            VideoPlaylistScreen(
                topicId = args.topicId,
                topicName = args.topicName,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 6. GLOBAL SAVED REVISION DASHBOARD
        composable<ScreenDestination.GlobalBookmarks> {
            GlobalBookmarksScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
