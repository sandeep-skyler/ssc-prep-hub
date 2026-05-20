// Location: app/src/main/java/com/sscprephub/app/presentation/components/MutationDialogs.kt
package com.sscprephub.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// ------------------------------------------------------------------------
// TOPIC ADD/EDIT COMPONENT OVERLAY
// ------------------------------------------------------------------------
@Composable
fun TopicInputDialog(
    initialName: String = "",
    title: String,
    onSave: (name: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (isError && it.isNotBlank()) isError = false
                    },
                    label = { Text("Topic Name") },
                    placeholder = { Text("e.g., Trigonometry, Synonyms") },
                    isError = isError,
                    supportingText = {
                        if (isError) Text("Topic name cannot be left empty", color = MaterialTheme.colorScheme.error)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onSave(name.trim())
                    } else {
                        isError = true
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ------------------------------------------------------------------------
// PREVIOUS YEAR PAPER (PYT) ADD/EDIT OVERLAY
// ------------------------------------------------------------------------
@Composable
fun PYTPaperInputDialog(
    initialTitle: String = "",
    initialYear: String = "",
    initialLink: String = "",
    initialDesc: String = "",
    dialogTitle: String,
    onSave: (title: String, year: Int, link: String, description: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var year by remember { mutableStateOf(initialYear) }
    var link by remember { mutableStateOf(initialLink) }
    var desc by remember { mutableStateOf(initialDesc) }
    
    var titleError by remember { mutableStateOf(false) }
    var yearError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = dialogTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; if(titleError) titleError = false },
                    label = { Text("Paper Title") },
                    placeholder = { Text("e.g., CGL 2023 Tier-1 Shift 1") },
                    isError = titleError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it; if(yearError) yearError = false },
                    label = { Text("Exam Year") },
                    placeholder = { Text("e.g., 2023") },
                    isError = yearError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = link,
                    onValueChange = { link = it },
                    label = { Text("Resource Link (Optional URL)") },
                    placeholder = { Text("https://example.com/mock-pdf") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Notes / Description") },
                    placeholder = { Text("e.g., 25 Questions, high difficulty logic matrix") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedYear = year.toIntOrNull()
                    titleError = title.isBlank()
                    yearError = parsedYear == null || parsedYear < 1990 || parsedYear > 2030

                    if (!titleError && !yearError && parsedYear != null) {
                        onSave(title.trim(), parsedYear, link.trim(), desc.trim())
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ------------------------------------------------------------------------
// YOUTUBE PLAYLIST ADD/EDIT OVERLAY
// ------------------------------------------------------------------------
@Composable
fun PlaylistInputDialog(
    initialTitle: String = "",
    initialUrl: String = "",
    initialInstructor: String = "",
    initialNotes: String = "",
    dialogTitle: String,
    onSave: (title: String, url: String, instructor: String, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var url by remember { mutableStateOf(initialUrl) }
    var instructor by remember { mutableStateOf(initialInstructor) }
    var notes by remember { mutableStateOf(initialNotes) }

    var titleError by remember { mutableStateOf(false) }
    var urlError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = dialogTitle, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; if(titleError) titleError = false },
                    label = { Text("Playlist Title") },
                    placeholder = { Text("e.g., Complete Number System") },
                    isError = titleError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it; if(urlError) urlError = false },
                    label = { Text("YouTube Video / Playlist Link") },
                    placeholder = { Text("https://www.youtube.com/watch?v=...") },
                    isError = urlError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = instructor,
                    onValueChange = { instructor = it },
                    label = { Text("Instructor Name") },
                    placeholder = { Text("e.g., Abhinay Sir, Gagan Pratap") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Personal Study Notes") },
                    placeholder = { Text("e.g., Focus on short tricks for formulas in part 4") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    titleError = title.isBlank()
                    urlError = url.isBlank()

                    if (!titleError && !urlError) {
                        onSave(title.trim(), url.trim(), instructor.trim(), notes.trim())
                    }
                }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
