// Location: app/src/main/java/com/sscprephub/app/presentation/screen/HomeScreen.kt
package com.sscprephub.app.presentation.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sscprephub.app.data.local.entity.TopicEntity
import com.sscprephub.app.domain.model.DashboardStats
import com.sscprephub.app.domain.model.SubjectWithStats
import com.sscprephub.app.presentation.components.StatCard
import com.sscprephub.app.presentation.theme.PendingAmber
import com.sscprephub.app.presentation.theme.ProgressGreen
import com.sscprephub.app.presentation.viewmodel.PrepViewModel

@Composable
fun HomeScreen(
    viewModel: PrepViewModel,
    onSubjectClick: (id: Int, name: String) -> Unit,
    onRecentTopicClick: (id: Int, name: String, subjectName: String) -> Unit
) {
    val stats by viewModel.dashboardStats.collectAsState()
    val subjectsWithStats by viewModel.subjectsWithStats.collectAsState()
    val recentTopics by viewModel.recentlyUpdatedTopics.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("SSC Prep Hub", fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp)
                        Text("Track. Study. Clear.", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. STATS OVERVIEW SECTION
            item {
                StatsGridSection(stats = stats)
            }

            // 2. CORE SUBJECT CARDS MATRIX
            item {
                Text(
                    text = "Core Subjects",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
                SubjectsGrid(
                    subjectsList = subjectsWithStats,
                    onSubjectClick = onSubjectClick
                )
            }

            // 3. RECENT ACTIVITY COMPONENT
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Activity",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (recentTopics.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No topics worked on recently. Open a subject to begin!", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(recentTopics, key = { it.id }) { topic ->
                    // Find subject name matching topic
                    val parentSubject = subjectsWithStats.find { it.subject.id == topic.subjectId }
                    val subjectName = parentSubject?.subject?.name ?: "Subject"

                    RecentTopicItem(
                        topic = topic,
                        subjectName = subjectName,
                        onClick = { onRecentTopicClick(topic.id, topic.name, subjectName) }
                    )
                }
            }
        }
    }
}

// ------------------------------------------------------------------------
// SUB-LAYOUT GRIDS
// ------------------------------------------------------------------------
@Composable
fun StatsGridSection(stats: DashboardStats) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard("Total Topics", "${stats.totalTopics}", Icons.Default.MenuBook, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
            StatCard("Completed", "${stats.completedTopics}", Icons.Default.CheckCircle, ProgressGreen, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard("Mock Papers", "${stats.totalPYTPapers}", Icons.Default.Description, PendingAmber, Modifier.weight(1f))
            StatCard("Playlists", "${stats.totalPlaylists}", Icons.Default.PlayCircle, Color(0xFFFF5722), Modifier.weight(1f))
        }
    }
}

@Composable
fun SubjectsGrid(
    subjectsList: List<SubjectWithStats>,
    onSubjectClick: (id: Int, name: String) -> Unit
) {
    // Height constraint map helper since Nested Vertical Grids are problematic inside LazyColumns
    val chunks = subjectsList.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        for (row in chunks) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                for (item in row) {
                    SubjectGridItem(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = { onSubjectClick(item.subject.id, item.subject.name) }
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun SubjectGridItem(
    item: SubjectWithStats,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val mappedIcon = when (item.subject.icon) {
        "psychology" -> Icons.Default.Psychology
        "translate" -> Icons.Default.Translate
        "functions" -> Icons.Default.Functions
        "public" -> Icons.Default.Public
        else -> Icons.Default.Book
    }

    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = mappedIcon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp)) {
                    CircularProgressIndicator(
                        progress = { item.progressPercentage / 100f },
                        color = ProgressGreen,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 3.dp,
                    )
                    Text("${item.progressPercentage}%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = item.subject.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${item.completedTopics}/${item.totalTopics} Topics Done",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RecentTopicItem(
    topic: TopicEntity,
    subjectName: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (topic.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if (topic.isCompleted) ProgressGreen else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = topic.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subjectName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
