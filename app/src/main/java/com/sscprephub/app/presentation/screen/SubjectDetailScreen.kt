// Location: app/src/main/java/com/sscprephub/app/presentation/screen/SubjectDetailScreen.kt
package com.sscprephub.app.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sscprephub.app.data.local.entity.TopicEntity
import com.sscprephub.app.presentation.components.AppTopBar
import com.sscprephub.app.presentation.components.CompletionToggle
import com.sscprephub.app.presentation.components.EmptyState
import com.sscprephub.app.presentation.components.SearchBar
import com.sscprephub.app.presentation.viewmodel.PrepViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailScreen(
    subjectId: Int,
    subjectName: String,
    viewModel: PrepViewModel,
    onBackClick: () -> Unit,
    onTopicClick: (id: Int, name: String) -> Unit,
    onManagePyqClick: () -> Unit,
    onManagePlaylistsClick: () -> Unit
) {
    var selectedFilterTab by remember { mutableIntStateOf(0) } // 0 = All, 1 = Pending, 2 = Completed
    val searchQuery by viewModel.topicSearchQuery.collectAsState()

    // Sync subject selection into ViewModel pipeline
    LaunchedEffect(subjectId) {
        viewModel.selectSubject(subjectId)
    }

    val topics by viewModel.currentTopicsList.collectAsState()

    // Filter topics down to tabs on the client side
    val filteredTopics = remember(topics, selectedFilterTab) {
        topics.filter { topic ->
            when (selectedFilterTab) {
                1 -> !topic.isCompleted
                2 -> topic.isCompleted
                else -> true
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = subjectName,
                showBackButton = true,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. QUICK NAVIGATION SHORTCUTS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onManagePyqClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mock PYQs", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onManagePlaylistsClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Playlists", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // 2. LIVE QUERY SEARCH & STATE TABS
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.updateTopicSearch(it) },
                placeholder = "Search topics in $subjectName...",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            TabRow(
                selectedTabIndex = selectedFilterTab,
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                val tabs = listOf("All", "Pending", "Completed")
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedFilterTab == index,
                        onClick = { selectedFilterTab = index },
                        text = { Text(title, fontWeight = FontWeight.Medium) }
                    )
                }
            }

            // 3. CORE SCROLL CONTAINER
            if (filteredTopics.isEmpty()) {
                val msg = if (searchQuery.isNotEmpty()) "No topics match your search criteria." else "No topics found here."
                EmptyState(
                    message = msg,
                    icon = Icons.Default.SearchOff,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = filteredTopics, key = { it.id }) { topic ->
                        TopicRowItem(
                            topic = topic,
                            onItemClick = { onTopicClick(topic.id, topic.name) },
                            onToggleCompletion = { viewModel.toggleTopicCompletion(topic.id, !topic.isCompleted) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopicRowItem(
    topic: TopicEntity,
    onItemClick: () -> Unit,
    onToggleCompletion: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompletionToggle(
                isCompleted = topic.isCompleted,
                onToggle = onToggleCompletion
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = topic.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
