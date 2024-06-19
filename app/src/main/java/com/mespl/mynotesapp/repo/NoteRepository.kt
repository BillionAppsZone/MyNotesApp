package com.mespl.mynotesapp.repo

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mespl.mynotesapp.dao.NoteDao
import com.mespl.mynotesapp.db.Note
import kotlinx.coroutines.tasks.await

class NoteRepository(private val noteDao: NoteDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }

    suspend fun saveNoteToFirestore(note: Note) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val encryptedNote = mapOf(
                "title" to note.encryptedTitle,
                "description" to note.encryptedDescription,
                "timestamp" to note.timeStamp
            )
            firestore.collection("notes").add(encryptedNote).await()

        }

        suspend fun getNotesFromFirestore(): List<Note> {
            val snapshot = firestore.collection("notes").get().await()
            return snapshot.documents.mapNotNull { document ->
                document.toObject(Note::class.java)
            }
        }
    }
}
