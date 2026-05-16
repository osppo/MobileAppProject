package com.Notes.notes


import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNotesScreen(
    onAddClick: () -> Unit,
    onNoteClick: (Int) -> Unit,
    onPracticeClick: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { NotesDatabaseHelper(context) }

    var notes by remember {
        mutableStateOf(dbHelper.getAllNotes())
    }

    fun refreshNotes() {
        notes = dbHelper.getAllNotes()
    }
    LaunchedEffect(Unit) {
        refreshNotes()
    }
    Scaffold(
        topBar = {
            TopAppBar(

                title = {
                    Text(stringResource(R.string.main_title))
                },

                actions = {

                    TextButton(

                        onClick = {

                            val currentLanguage =
                                LanguageManager.getSavedLanguage(context)

                            if (currentLanguage == "en") {

                                LanguageManager.setLocale(
                                    context,
                                    "ar"
                                )

                            } else {

                                LanguageManager.setLocale(
                                    context,
                                    "en"
                                )
                            }
                        }

                    ) {

                        Text(
                            stringResource(R.string.language)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_note)
                )
            }
        }
    ) { innerPadding ->
        if (notes.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.no_notes),
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = stringResource(R.string.create_note_message),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = notes,
                    key = { note -> note.id }
                ) { note ->
                    NoteCard(
                        note = note,
                        onClick = {
                            onNoteClick(note.id)
                        },
                        onDeleteClick = {
                            dbHelper.deleteNote(note.id)
                            refreshNotes()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.type_prefix, note.type),
                    style = MaterialTheme.typography.bodySmall
                )

                when (note.type) {
                    NoteType.NORMAL -> {
                        Text(
                            text = note.content ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    NoteType.QA -> {
                        Text(
                            text = stringResource(R.string.qa_practice_msg),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDeleteClick
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_note_desc)
                )
            }
        }
    }
}