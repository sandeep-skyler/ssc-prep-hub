// Location: app/src/main/java/com/sscprephub/app/presentation/AppNavigationGraph.kt
package com.sscprephub.app.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
        startDestination = ScreenDestination.DASHBOARD,
        modifier = modifier
    ) {
        // 1. MAIN METRICS DASHBOARD (Mapped to HomeScreen)
        // 1. MAIN METRICS DASHBOARD (Mapped to HomeScreen)
        composable(ScreenDestination.DASHBOARD) {
            HomeScreen(
                viewModel = viewModel,
                onSubjectClick = { id, name ->
                    navController.navigate(ScreenDestination.createSubjectDetailRoute(id, name))
                },
                onRecentTopicClick = { id, name ->
                    navController.navigate(ScreenDestination.createTopicDetailRoute(id, name))
                }
            )
        }
        }

        // 2. SUBJECT DETAILS TRACKER
        composable(
            route = ScreenDestination.SUBJECT_DETAIL,
            arguments = listOf(
                navArgument("subjectId") { type = NavType.IntType },
                navArgument("subjectName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getInt("subjectId") ?: 0
            val subjectName = backStackEntry.arguments?.getString("subjectName") ?: ""
            
            SubjectDetailScreen(
                subjectId = subjectId,
                subjectName = subjectName,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onTopicClick = { id, name ->
                    navController.navigate(ScreenDestination.createTopicDetailRoute(id, name))
                },
                onManagePyqClick = {
                    navController.navigate(ScreenDestination.createPyqPaperListRoute(subjectId, subjectName))
                },
                onManagePlaylistsClick = {
                    navController.navigate(ScreenDestination.createVideoPlaylistRoute(subjectId, subjectName))
                }
            )
        }

        // 3. TOPIC NOTEBOOK FOCUS WORKSPACE
        composable(
            route = ScreenDestination.TOPIC_DETAIL,
            arguments = listOf(
                navArgument("topicId") { type = NavType.IntType },
                navArgument("topicName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getInt("topicId") ?: 0
            val topicName = backStackEntry.arguments?.getString("topicName") ?: ""
            
            TopicDetailScreen(
                topicId = topicId,
                topicName = topicName,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 4. PREVIOUS YEAR PAPERS EXPLORER
        composable(
            route = ScreenDestination.PYQ_PAPER_LIST,
            arguments = listOf(
                navArgument("topicId") { type = NavType.IntType },
                navArgument("topicName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getInt("topicId") ?: 0
            val topicName = backStackEntry.arguments?.getString("topicName") ?: ""
            
            PYQPaperListScreen(
                topicId = topicId,
                topicName = topicName,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 5. YOUTUBE VIDEO PLAYLIST TRACKER
        composable(
            route = ScreenDestination.VIDEO_PLAYLIST,
            arguments = listOf(
                navArgument("topicId") { type = NavType.IntType },
                navArgument("topicName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getInt("topicId") ?: 0
            val topicName = backStackEntry.arguments?.getString("topicName") ?: ""
            
            VideoPlaylistScreen(
                topicId = topicId,
                topicName = topicName,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 6. GLOBAL SAVED REVISION DASHBOARD (Mapped to BookmarksScreen)
        composable(ScreenDestination.GLOBAL_BOOKMARKS) {
            BookmarksScreen(
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
