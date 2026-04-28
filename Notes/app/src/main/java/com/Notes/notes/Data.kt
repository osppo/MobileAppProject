package com.Notes.notes

data class Note(
    val id: Int = 0,
    val type: String,
    val title: String,
    val content: String?
)

data class QaPair(
    val id: Int = 0,
    val noteId: Int = 0,
    val question: String,
    val answer: String
)

object NoteType {
    const val NORMAL = "normal"
    const val QA = "qa"
}