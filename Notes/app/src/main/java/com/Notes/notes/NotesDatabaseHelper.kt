package com.Notes.notes

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "studynotes.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_NOTES = "notes"
        private const val COLUMN_NOTE_ID = "id"
        private const val COLUMN_NOTE_TYPE = "type"
        private const val COLUMN_NOTE_TITLE = "title"
        private const val COLUMN_NOTE_CONTENT = "content"

        private const val TABLE_QA_PAIRS = "qa_pairs"
        private const val COLUMN_QA_ID = "id"
        private const val COLUMN_QA_NOTE_ID = "note_id"
        private const val COLUMN_QA_QUESTION = "question"
        private const val COLUMN_QA_ANSWER = "answer"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createNotesTable = """
            CREATE TABLE $TABLE_NOTES (
                $COLUMN_NOTE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOTE_TYPE TEXT NOT NULL,
                $COLUMN_NOTE_TITLE TEXT NOT NULL,
                $COLUMN_NOTE_CONTENT TEXT
            )
        """.trimIndent()

        val createQaPairsTable = """
            CREATE TABLE $TABLE_QA_PAIRS (
                $COLUMN_QA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_QA_NOTE_ID INTEGER NOT NULL,
                $COLUMN_QA_QUESTION TEXT NOT NULL,
                $COLUMN_QA_ANSWER TEXT NOT NULL
            )
        """.trimIndent()

        db.execSQL(createNotesTable)
        db.execSQL(createQaPairsTable)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_QA_PAIRS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    fun insertNormalNote(
        title: String,
        content: String
    ): Long {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_NOTE_TYPE, NoteType.NORMAL)
            put(COLUMN_NOTE_TITLE, title)
            put(COLUMN_NOTE_CONTENT, content)
        }

        return db.insert(TABLE_NOTES, null, values)
    }

    fun insertQaNote(
        title: String,
        pairs: List<QaPair>
    ): Long {
        val db = writableDatabase
        var noteId = -1L

        db.beginTransaction()

        try {
            val noteValues = ContentValues().apply {
                put(COLUMN_NOTE_TYPE, NoteType.QA)
                put(COLUMN_NOTE_TITLE, title)
                putNull(COLUMN_NOTE_CONTENT)
            }

            noteId = db.insert(TABLE_NOTES, null, noteValues)

            if (noteId != -1L) {
                for (pair in pairs) {
                    val pairValues = ContentValues().apply {
                        put(COLUMN_QA_NOTE_ID, noteId)
                        put(COLUMN_QA_QUESTION, pair.question)
                        put(COLUMN_QA_ANSWER, pair.answer)
                    }

                    db.insert(TABLE_QA_PAIRS, null, pairValues)
                }

                db.setTransactionSuccessful()
            }
        } finally {
            db.endTransaction()
        }

        return noteId
    }

    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val db = readableDatabase

        val cursor = db.query(
            TABLE_NOTES,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_NOTE_ID DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                notes.add(cursorToNote(it))
            }
        }

        return notes
    }

    fun getNoteById(noteId: Int): Note? {
        val db = readableDatabase

        val cursor = db.query(
            TABLE_NOTES,
            null,
            "$COLUMN_NOTE_ID = ?",
            arrayOf(noteId.toString()),
            null,
            null,
            null
        )

        cursor.use {
            return if (it.moveToFirst()) {
                cursorToNote(it)
            } else {
                null
            }
        }
    }

    fun getQaPairs(noteId: Int): List<QaPair> {
        val pairs = mutableListOf<QaPair>()
        val db = readableDatabase

        val cursor = db.query(
            TABLE_QA_PAIRS,
            null,
            "$COLUMN_QA_NOTE_ID = ?",
            arrayOf(noteId.toString()),
            null,
            null,
            "$COLUMN_QA_ID ASC"
        )

        cursor.use {
            while (it.moveToNext()) {
                pairs.add(cursorToQaPair(it))
            }
        }

        return pairs
    }

    fun getQaPairCount(noteId: Int): Int {
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_QA_PAIRS WHERE $COLUMN_QA_NOTE_ID = ?",
            arrayOf(noteId.toString())
        )

        cursor.use {
            return if (it.moveToFirst()) {
                it.getInt(0)
            } else {
                0
            }
        }
    }

    fun deleteNote(noteId: Int): Int {
        val db = writableDatabase

        db.delete(
            TABLE_QA_PAIRS,
            "$COLUMN_QA_NOTE_ID = ?",
            arrayOf(noteId.toString())
        )

        return db.delete(
            TABLE_NOTES,
            "$COLUMN_NOTE_ID = ?",
            arrayOf(noteId.toString())
        )
    }

    fun updateNormalNote(
        noteId: Int,
        title: String,
        content: String
    ): Int {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(COLUMN_NOTE_TYPE, NoteType.NORMAL)
            put(COLUMN_NOTE_TITLE, title)
            put(COLUMN_NOTE_CONTENT, content)
        }

        db.delete(
            TABLE_QA_PAIRS,
            "$COLUMN_QA_NOTE_ID = ?",
            arrayOf(noteId.toString())
        )

        return db.update(
            TABLE_NOTES,
            values,
            "$COLUMN_NOTE_ID = ?",
            arrayOf(noteId.toString())
        )
    }

    fun updateQaNote(
        noteId: Int,
        title: String,
        pairs: List<QaPair>
    ): Int {
        val db = writableDatabase
        var updatedRows = 0

        db.beginTransaction()

        try {
            val values = ContentValues().apply {
                put(COLUMN_NOTE_TYPE, NoteType.QA)
                put(COLUMN_NOTE_TITLE, title)
                putNull(COLUMN_NOTE_CONTENT)
            }

            updatedRows = db.update(
                TABLE_NOTES,
                values,
                "$COLUMN_NOTE_ID = ?",
                arrayOf(noteId.toString())
            )

            db.delete(
                TABLE_QA_PAIRS,
                "$COLUMN_QA_NOTE_ID = ?",
                arrayOf(noteId.toString())
            )

            for (pair in pairs) {
                val pairValues = ContentValues().apply {
                    put(COLUMN_QA_NOTE_ID, noteId)
                    put(COLUMN_QA_QUESTION, pair.question)
                    put(COLUMN_QA_ANSWER, pair.answer)
                }

                db.insert(TABLE_QA_PAIRS, null, pairValues)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

        return updatedRows
    }

    private fun cursorToNote(cursor: Cursor): Note {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTE_ID))
        val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_TYPE))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE_TITLE))
        val content = getNullableString(cursor, COLUMN_NOTE_CONTENT)

        return Note(
            id = id,
            type = type,
            title = title,
            content = content
        )
    }

    private fun cursorToQaPair(cursor: Cursor): QaPair {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QA_ID))
        val noteId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QA_NOTE_ID))
        val question = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QA_QUESTION))
        val answer = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QA_ANSWER))

        return QaPair(
            id = id,
            noteId = noteId,
            question = question,
            answer = answer
        )
    }

    private fun getNullableString(
        cursor: Cursor,
        columnName: String
    ): String? {
        val index = cursor.getColumnIndexOrThrow(columnName)

        return if (cursor.isNull(index)) {
            null
        } else {
            cursor.getString(index)
        }
    }
}