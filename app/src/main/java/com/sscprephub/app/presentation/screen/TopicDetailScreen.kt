// Location: app/src/main/java/com/sscprephub/app/presentation/screen/TopicDetailScreen.kt
package com.sscprephub.app.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sscprephub.app.presentation.components.AppTopBar
import com.sscprephub.app.presentation.components.BookmarkButton
import com.sscprephub.app.presentation.viewmodel.PrepViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDetailScreen(
    topicId: Int,
    topicName: String,
    viewModel: PrepViewModel,
    onBackClick: () -> Unit
) {
    // Synchronize data context state
    LaunchedEffect(topicId) {
        viewModel.loadTopicDetails(topicId)
    }

    val currentTopic by viewModel.selectedTopicDetails.collectAsState()
    
    var isEditingNotes by remember { mutableStateOf(false) }
    var runningNotesText by remember { mutableStateOf("") }

    // Synchronize initial text values when entity updates
    LaunchedEffect(currentTopic) {
        currentTopic?.let {
            runningNotesText = it.importantNotes
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = topicName,
                showBackButton = true,
                onBackClick = onBackClick,
                actions = {
                    currentTopic?.let { topic ->
                        BookmarkButton(
                            isBookmarked = topic.isBookmarked,
                            onBookmarkToggle = { viewModel.toggleTopicBookmark(topic) }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        currentTopic?.let { topic ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. COMPLETION STATUS BANNER
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (topic.isCompleted) 
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        else 
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (topic.isCompleted) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                                contentDescription = null,
                                tint = if (topic.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (topic.isCompleted) "Completed Topic" else "In Progress",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Button(
                            onClick = { viewModel.toggleTopicCompletion(topic) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (topic.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(if (topic.isCompleted) "Mark Pending" else "Mark Complete", fontSize = 12.sp)
                        }
                    }
                }

                // 2. INTERACTIVE STUDY REVISION NOTEBOX
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Text(
                                text = "Revision Notes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = {
                                    if (isEditingNotes) {
                                        viewModel.updateTopicNotes(topic, runningNotesText)
                                    }
                                    isEditingNotes = !isEditingNotes
                                }
                            ) {
                                Icon(
                                    imageVector = if (isEditingNotes) Icons.Default.Save else Icons.Default.Edit,
                                    contentDescription = "Modify Note Context",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        if (isEditingNotes) {
                            OutlinedTextField(
                                value = runningNotesText,
                                onValueChange = { runningNotesText = it },
                                placeholder = { Text("Jot down core shortcut formulas, mnemonics, or common pitfalls...") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 6,
                                maxLines = 15,
                                shape = RoundedCornerShape(8.dp)
                            )
                        } else {
                            if (topic.importantNotes.isEmpty()) {
                                Text(
                                    text = "No custom revision notes configured yet. Tap the edit icon to store formulas or shortcuts!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            } else {
                                Text(
                                    text = topic.importantNotes,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }

                // 3. SEAMLESS WORKSPACE FORMULA HELPER
                RevisionHelperPanel()
            }
        }
    }
}

// ------------------------------------------------------------------------
// FORMULA ACCELERATOR SIDE PANEL PANEL
// ------------------------------------------------------------------------
@Composable
fun RevisionHelperPanel() {
    var rawInput1 by remember { mutableStateOf("") }
    var rawInput2 by remember { mutableStateOf("") }
    var speedResult by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Calculate, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Speed & Performance Calculator",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Quickly verify ratios, percentages, or average speed conversions ($2xy/(x+y)$) without exiting your revision context.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = rawInput1,
                    onValueChange = { rawInput1 = it },
                    label = { Text("Speed X (km/h)", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = rawInput2,
                    onValueChange = { rawInput2 = it },
                    label = { Text("Speed Y (km/h)", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val x = rawInput1.toDoubleOrNull()
                    val y = rawInput2.toDoubleOrNull()
                    if (x != null && y != null && (x + y) > 0) {
                        val avg = (2 * x * y) / (x + y)
                        speedResult = String.format("%.2f km/h", avg)
                    } else {
                        speedResult = "Invalid Inputs"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Compute Average Speed Formula", fontSize = 13.sp)
            }

            speedResult?.let { result ->
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Resulting Value: $result",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
