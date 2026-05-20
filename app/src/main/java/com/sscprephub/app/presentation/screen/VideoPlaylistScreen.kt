// Location: app/src/main/java/com/sscprephub/app/presentation/screen/VideoPlaylistScreen.kt
package com.sscprephub.app.presentation.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sscprephub.app.data.local.entity.YTPlaylistEntity
import com.sscprephub.app.presentation.components.AppTopBar
import com.sscprephub.app.presentation.components.BookmarkButton
import com.sscprephub.app.presentation.components.EmptyState
import com.sscprephub.app.presentation.viewmodel.PrepViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlaylistScreen(
    topicId: Int,
    topicName: String,
    viewModel: PrepViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val playlists by viewModel.currentYTPlaylists.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(topicId) {
        viewModel.selectTopic(topicId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Lectures: $topicName",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Playlist")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (playlists.isEmpty()) {
                EmptyState(
                    message = "No reference video playlists cataloged yet.",
                    icon = Icons.Default.PlayCircle,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = playlists, key = { it.id }) { playlist ->
                        PlaylistRowItem(
                            playlist = playlist,
                            onLaunchUrl = {
                                if (playlist.url.isNotEmpty()) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playlist.url))
                                    context.startActivity(intent)
                                }
                            },
                            onBookmarkToggle = { viewModel.togglePlaylistBookmark(playlist.id, !playlist.isBookmarked) }
                        )
                    }
                }
            }

            if (showAddDialog) {
                AddPlaylistDialog(
                    onDismiss = { showAddDialog = false },
                    onConfirm = { title, url, teacher, notes ->
                        viewModel.insertPlaylist(topicId, title, url, teacher, notes)
                        showAddDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun PlaylistRowItem(
    playlist: YTPlaylistEntity,
    onLaunchUrl: () -> Unit,
    onBookmarkToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PlayCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onLaunchUrl() }
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onLaunchUrl() }
            ) {
                Text(
                    text = playlist.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Instructor: ${playlist.instructor.ifBlank { "Unknown" }}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (playlist.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = playlist.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            BookmarkButton(
                isBookmarked = playlist.isBookmarked,
                onBookmarkToggle = onBookmarkToggle
            )
        }
    }
}

@Composable
fun AddPlaylistDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, url: String, instructor: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var instructor by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Link Study Playlist", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Playlist Title (e.g., Geometry Full)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("YouTube Playlist / Video URL") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = instructor, onValueChange = { instructor = it }, label = { Text("Educator Name") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Personal Study Strategy / Focus Points") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && url.isNotBlank()) {
                        onConfirm(title, url, instructor, notes)
                    }
                }
            ) { Text("Link Playlist") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
