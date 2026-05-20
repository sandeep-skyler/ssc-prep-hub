// Location: app/src/main/java/com/sscprephub/app/presentation/screen/PYQPaperListScreen.kt
package com.sscprephub.app.presentation.screen

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sscprephub.app.data.local.entity.PYTPaperEntity
import com.sscprephub.app.presentation.components.AppTopBar
import com.sscprephub.app.presentation.components.BookmarkButton
import com.sscprephub.app.presentation.components.EmptyState
import com.sscprephub.app.presentation.viewmodel.PrepViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PYQPaperListScreen(
    topicId: Int,
    topicName: String,
    viewModel: PrepViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val papers by viewModel.currentPYTPapers.collectAsState()
    val activeYearFilter by viewModel.pytYearFilter.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }

    // Synchronize current active topic context
    LaunchedEffect(topicId) {
        viewModel.selectTopic(topicId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "PYQs: $topicName",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add New Paper")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // YEAR FILTER CHIP ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val years = listOf(null, 2024, 2023, 2022)
                years.forEach { year ->
                    FilterChip(
                        selected = activeYearFilter == year,
                        onClick = { viewModel.setYearFilter(year) },
                        label = { Text(year?.toString() ?: "All Years") }
                    )
                }
            }

            // CORE PAPERS LISTCONTAINER
            if (papers.isEmpty()) {
                EmptyState(
                    message = if (activeYearFilter != null) "No papers found for the year $activeYearFilter." else "No papers linked to this topic yet.",
                    icon = Icons.Default.Description,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = papers, key = { it.id }) { paper ->
                        PYQPaperRowItem(
                            paper = paper,
                            onLinkClick = {
                                if (paper.link.isNotEmpty()) {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(paper.link))
                                    context.startActivity(intent)
                                }
                            },
                            onBookmarkToggle = { viewModel.togglePYTBookmark(paper.id, !paper.isBookmarked) }
                        )
                    }
                }
            }
        }

        // CUSTOM ADD DIALOG WORKSPACE
        if (showAddDialog) {
            AddPaperDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { title, year, link, desc ->
                    viewModel.insertPYTPaper(topicId, title, year, link, desc)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun PYQPaperRowItem(
    paper: PYTPaperEntity,
    onLinkClick: () -> Unit,
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onLinkClick() }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SuggestionChip(
                        onClick = onLinkClick,
                        label = { Text(paper.year.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = paper.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (paper.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = paper.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            BookmarkButton(
                isBookmarked = paper.isBookmarked,
                onBookmarkToggle = onBookmarkToggle
            )
        }
    }
}

@Composable
fun AddPaperDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, year: Int, link: String, description: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var yearStr by remember { mutableStateOf("2024") }
    var link by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Link PYQ Mock Set", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Paper Title (e.g., CGL Shift-1)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = yearStr, onValueChange = { yearStr = it }, label = { Text("Exam Year") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = link, onValueChange = { link = it }, label = { Text("Drive / Mock PDF URL") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Short Notes / Shift Analysis") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val year = yearStr.toIntOrNull() ?: 2024
                    if (title.isNotBlank()) {
                        onConfirm(title, year, link, description)
                    }
                }
            ) { Text("Save Paper") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
