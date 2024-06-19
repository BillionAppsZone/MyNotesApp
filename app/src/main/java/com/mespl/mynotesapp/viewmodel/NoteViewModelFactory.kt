package com.mespl.mynotesapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NoteViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModal::class.java)) {
            return NoteViewModal(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}