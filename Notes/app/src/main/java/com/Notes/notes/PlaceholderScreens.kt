package com.Notes.notes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.material3.Card
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
@Composable
fun CreateNoteTypeScreen(
    onNormalClick: () -> Unit,
    onQaClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Choose Note Type",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNormalClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Normal Note")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onQaClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Q&A Note")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: Int,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { NotesDatabaseHelper(context) }

    var note by remember {
        mutableStateOf<Note?>(null)
    }

    var qaPool by remember {
        mutableStateOf<List<QaPair>>(emptyList())
    }

    var currentIndex by remember {
        mutableStateOf(0)
    }

    var showAnswer by remember {
        mutableStateOf(false)
    }

    var userAnswer by remember {
        mutableStateOf("")
    }

    var feedbackMessage by remember {
        mutableStateOf("")
    }

    var isCorrect by remember {
        mutableStateOf<Boolean?>(null)
    }

    var score by remember {
        mutableStateOf(0)
    }

    var answeredCurrentQuestion by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    fun resetCurrentQuestionState() {
        userAnswer = ""
        feedbackMessage = ""
        isCorrect = null
        showAnswer = false
        answeredCurrentQuestion = false
    }

    LaunchedEffect(noteId) {
        val loadedNote = dbHelper.getNoteById(noteId)
        note = loadedNote

        if (loadedNote?.type == NoteType.QA) {
            qaPool = dbHelper.getQaPairs(noteId).shuffled()
            currentIndex = 0
            score = 0
            resetCurrentQuestionState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Note Details")
                }
            )
        }
    ) { innerPadding ->
        val currentNote = note

        if (currentNote == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text("Note not found")

                Spacer(modifier = Modifier.height(12.dp))

                Button(onClick = onBackClick) {
                    Text("Back")
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(
                    text = currentNote.title,
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Type: ${currentNote.type}",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                when (currentNote.type) {
                    NoteType.NORMAL -> {
                        Text(
                            text = "Content",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentNote.content ?: "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    NoteType.QA -> {
                        if (qaPool.isEmpty()) {
                            Text(
                                text = "No questions found in this Q&A note.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        } else {
                            val currentPair = qaPool[currentIndex]
                            fun submitAnswer() {
                                val user = userAnswer.trim()
                                val correct = currentPair.answer.trim()

                                if (answeredCurrentQuestion) {
                                    return
                                }

                                if (user.isBlank()) {
                                    feedbackMessage = "Enter an answer first."
                                    isCorrect = null
                                } else {
                                    val matched = user.equals(correct, ignoreCase = true)

                                    isCorrect = matched
                                    answeredCurrentQuestion = true
                                    showAnswer = true

                                    if (matched) {
                                        score++
                                        feedbackMessage = "Correct answer."
                                    } else {
                                        feedbackMessage = "Wrong answer."
                                    }
                                }
                            }
                            Text(
                                text = "Question ${currentIndex + 1} of ${qaPool.size}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Score: $score",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = currentPair.question,
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    if (showAnswer) {
                                        Spacer(modifier = Modifier.height(12.dp))

                                        Text(
                                            text = "Correct Answer:",
                                            style = MaterialTheme.typography.titleMedium
                                        )

                                        Text(
                                            text = currentPair.answer,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = userAnswer,
                                onValueChange = {
                                    userAnswer = it

                                    if (!answeredCurrentQuestion) {
                                        feedbackMessage = ""
                                        isCorrect = null
                                    }
                                },
                                label = {
                                    Text("Your Answer")
                                },
                                enabled = !answeredCurrentQuestion,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        submitAnswer()
                                        focusManager.clearFocus()
                                    }
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    submitAnswer()
                                    focusManager.clearFocus()
                                },
                                enabled = !answeredCurrentQuestion,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Submit Answer")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (feedbackMessage.isNotEmpty()) {
                                Text(
                                    text = feedbackMessage,
                                    color = when (isCorrect) {
                                        true -> MaterialTheme.colorScheme.primary
                                        false -> MaterialTheme.colorScheme.error
                                        null -> MaterialTheme.colorScheme.error
                                    },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    showAnswer = !showAnswer
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    if (showAnswer) {
                                        "Hide Answer"
                                    } else {
                                        "Show Answer"
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (currentIndex < qaPool.lastIndex) {
                                        currentIndex++
                                        resetCurrentQuestionState()
                                    }
                                },
                                enabled = currentIndex < qaPool.lastIndex,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Next Random Question")
                            }

                            if (currentIndex == qaPool.lastIndex) {
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "No more new questions in this session.",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        qaPool = qaPool.shuffled()
                                        currentIndex = 0
                                        score = 0
                                        resetCurrentQuestionState()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Restart Random Session")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back")
                }
            }
        }
    }
}
@Composable
fun NormalNoteEditorScreen(
    onSaved: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { NotesDatabaseHelper(context) }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Create Normal Note",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                errorMessage = ""
            },
            label = {
                Text("Title")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = content,
            onValueChange = {
                content = it
                errorMessage = ""
            },
            label = {
                Text("Content")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (title.isBlank() || content.isBlank()) {
                    errorMessage = "Title and content are required."
                } else {
                    dbHelper.insertNormalNote(
                        title = title.trim(),
                        content = content.trim()
                    )

                    onSaved()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}
@Composable
fun QaNoteEditorScreen(
    onSaved: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { NotesDatabaseHelper(context) }

    var title by remember { mutableStateOf("") }
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val questionFocusRequester = remember { FocusRequester() }
    val answerFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val pairs = remember {
        mutableStateListOf<QaPair>()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Create Q&A Note",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                errorMessage = ""
            },
            label = {
                Text("Note Title")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    questionFocusRequester.requestFocus()
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = question,
            onValueChange = {
                question = it
                errorMessage = ""
            },
            label = {
                Text("Question")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    answerFocusRequester.requestFocus()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(questionFocusRequester)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = answer,
            onValueChange = {
                answer = it
                errorMessage = ""
            },
            label = {
                Text("Answer")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(answerFocusRequester)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (question.isBlank() || answer.isBlank()) {
                    errorMessage = "Question and answer are required before adding."
                } else {
                    pairs.add(
                        QaPair(
                            question = question.trim(),
                            answer = answer.trim()
                        )
                    )

                    question = ""
                    answer = ""
                    errorMessage = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Q&A Pair")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Added Questions: ${pairs.size}",
            style = MaterialTheme.typography.titleMedium
        )

        pairs.forEachIndexed { index, pair ->
            Text(
                text = "${index + 1}. ${pair.question}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (title.isBlank()) {
                    errorMessage = "Title is required."
                    return@Button
                }

                val finalPairs = pairs.toMutableList()

                if (question.isNotBlank() && answer.isNotBlank()) {
                    finalPairs.add(
                        QaPair(
                            question = question.trim(),
                            answer = answer.trim()
                        )
                    )
                }

                if (finalPairs.isEmpty()) {
                    errorMessage = "Add at least one Q&A pair."
                } else {
                    dbHelper.insertQaNote(
                        title = title.trim(),
                        pairs = finalPairs
                    )

                    onSaved()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Q&A Note")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}



@Composable
fun PracticeScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Practice Screen")

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Settings Screen")

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onBackClick) {
            Text("Back")
        }
    }
}