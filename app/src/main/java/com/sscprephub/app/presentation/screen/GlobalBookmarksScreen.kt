// Location: app/src/main/java/com/sscprephub/app/presentation/screen/GlobalBookmarksScreen.kt
package com.sscprephub.app.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.sscprephub.app.presentation.components.EmptyState
import com.sscprephub.app.presentation.viewmodel.PrepViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalBookmarksScreen(
    viewModel: PrepViewModel,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Revision Bookmarks", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Displays a clean placeholder for empty bookmarked states
            EmptyState(
                message = "No bookmarked topics, papers, or video playlists found.",
                icon = Icons.Default.Bookmark,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
