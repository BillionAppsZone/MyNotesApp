package com.mespl.mynotesapp.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.mespl.mynotesapp.R
import com.mespl.mynotesapp.db.Note
import com.mespl.mynotesapp.db.NoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume

class BackupWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private val auth = FirebaseAuth.getInstance()
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val notes = getAllNotes()
            val backupFile = File(applicationContext.filesDir, "notes_backup.txt")

            backupFile.bufferedWriter().use { out ->
                notes.forEach {
                    out.write("${it.encryptedTitle},${it.encryptedDescription},${it.timeStamp}\n")
                }
            }

            if (!backupFile.exists()) {
                // File not created
                throw Exception("Backup file does not exist after creation.")
            }
            val storageRef =
                FirebaseStorage.getInstance().reference.child("backups/${backupFile.name}")

            val currentUser = auth.currentUser
            if (currentUser != null) {
                storageRef.putFile(Uri.fromFile(backupFile)).await()
            }


            withContext(Dispatchers.Main) {
                val notification = NotificationCompat.Builder(applicationContext, "default")
                    .setSmallIcon(R.drawable.ic_launcher).setContentTitle("Backup Done")
                    .setContentText("" + notes[0].encryptedTitle)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).build()
                if (ActivityCompat.checkSelfPermission(
                        applicationContext, Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    NotificationManagerCompat.from(applicationContext).notify(1, notification)
                }
            }
            Result.success()
        } catch (e: Exception) {
            println(e.localizedMessage)
            Result.failure()
        }
    }

    private suspend fun getAllNotes(): List<Note> {
        return suspendCancellableCoroutine { continuation ->
            val notesLiveData: LiveData<List<Note>> =
                NoteDatabase.getDatabase(applicationContext).noteDao().getAllNotes()
            val observer = object : Observer<List<Note>> {
                override fun onChanged(value: List<Note>) {
                    value.let {
                        continuation.resume(it)
                        notesLiveData.removeObserver(this)
                    }
                }

            }
            Handler(Looper.getMainLooper()).post {
                notesLiveData.observeForever(observer)
                continuation.invokeOnCancellation {
                    notesLiveData.removeObserver(observer)
                }
            }

        }
    }
}
