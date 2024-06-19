package com.mespl.mynotesapp.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mespl.mynotesapp.R
import com.mespl.mynotesapp.adapter.NoteAdapter
import com.mespl.mynotesapp.adapter.NoteClickDeleteInterface
import com.mespl.mynotesapp.adapter.NoteClickInterface
import com.mespl.mynotesapp.db.Note
import com.mespl.mynotesapp.viewmodel.AuthViewModel
import com.mespl.mynotesapp.viewmodel.NoteViewModal
import com.mespl.mynotesapp.viewmodel.NoteViewModelFactory
import com.mespl.mynotesapp.worker.scheduleBackup

class MainActivity : AppCompatActivity(), NoteClickInterface, NoteClickDeleteInterface {

    private lateinit var authViewModel: AuthViewModel

    lateinit var noteViewModel: NoteViewModal
    lateinit var notesRV: RecyclerView
    lateinit var addFAB: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        notesRV = findViewById(R.id.recycler_view)
        addFAB = findViewById(R.id.fab)
        notesRV.layoutManager = LinearLayoutManager(this)
        val noteRVAdapter = NoteAdapter(this, this, this)
        notesRV.adapter = noteRVAdapter
        val factory = NoteViewModelFactory(application)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        noteViewModel = ViewModelProvider(
            this,
            factory
        )[NoteViewModal::class.java]

        if (authViewModel.getCurrentUser() == null) {
            // Navigate to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        noteViewModel.allNotes. observe(this, Observer { list ->
            list?.let {
                noteRVAdapter.updateList(it)
            }
        })
        addFAB.setOnClickListener {
            val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
            startActivity(intent)
        }
        scheduleBackup(this)

    }

    override fun onNoteClick(note: Note) {
        val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
        intent.putExtra("noteType", "Edit")
        intent.putExtra("noteTitle", note.encryptedTitle)
        intent.putExtra("noteDescription", note.encryptedDescription)
        intent.putExtra("noteId", note.id)
        startActivity(intent)
    }

    override fun onDeleteIconClick(note: Note) {
        noteViewModel.deleteNote(note)
        Toast.makeText(this, "${note.encryptedTitle} Deleted", Toast.LENGTH_LONG).show()
    }

}
