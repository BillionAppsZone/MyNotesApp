package com.mespl.mynotesapp.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.*
import com.google.firebase.auth.FirebaseUser

class AuthRepository {

    private val auth: FirebaseAuth by lazy { getInstance() }

    fun login(email: String, password: String): LiveData<Result<FirebaseUser>> {
        val result = MutableLiveData<Result<FirebaseUser>>()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        result.value = Result.success(user)
                    } else {
                        result.value = Result.failure(Exception("User is null after successful authentication"))
                    }
                } else {
                    result.value = Result.failure(task.exception ?: Exception("Unknown error"))
                }
            }
        return result
    }

    fun register(email: String, password: String): LiveData<Result<FirebaseUser>> {
        val result = MutableLiveData<Result<FirebaseUser>>()
        if (email.isEmpty() || password.isEmpty()) {
            result.value = Result.failure(IllegalArgumentException("Email or password is empty"))
            return result
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        result.value = Result.success(user)
                    } else {
                        result.value = Result.failure(Exception("User is null after successful registration"))
                    }
                } else {
                    result.value = Result.failure(task.exception ?: Exception("Unknown error"))
                }
            }
        return result
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun logout() {
        auth.signOut()
    }
}
