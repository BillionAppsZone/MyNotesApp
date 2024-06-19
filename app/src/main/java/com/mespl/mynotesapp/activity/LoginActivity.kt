package com.mespl.mynotesapp.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.mespl.mynotesapp.R
import com.mespl.mynotesapp.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    companion object {
        private const val REQUEST_CODE_POST_NOTIFICATIONS = 1001
        private const val REQUEST_CODE_STORAGE = 1002
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            authViewModel.login(email, password).observe(this) { result ->
                result.onSuccess {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }.onFailure { exception ->
                    Toast.makeText(this, "Login failed: ${exception.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            authViewModel.register(email, password).observe(this) { result ->
                result.onSuccess {
                    // Navigate to main activity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }.onFailure { exception ->
                    Toast.makeText(
                        this,
                        "Registration failed: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        if (!checkPermissions()) {
            requestPermissions()
        }


    }

    private fun checkPermissions(): Boolean {
        val permission2 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            REQUEST_CODE_STORAGE
        )
    }
}
