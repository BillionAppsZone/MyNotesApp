package com.mespl.mynotesapp.viewmodel

import com.mespl.mynotesapp.repo.AuthRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AuthRepository by lazy { AuthRepository() }

    fun login(email: String, password: String): LiveData<Result<FirebaseUser>> {
        return repository.login(email, password)
    }

    fun register(email: String, password: String): LiveData<Result<FirebaseUser>> {
        return repository.register(email, password)
    }

    fun getCurrentUser(): FirebaseUser? {
        return repository.getCurrentUser()
    }

    fun logout() {
        repository.logout()
    }
}
