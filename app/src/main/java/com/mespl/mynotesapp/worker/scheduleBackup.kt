package com.mespl.mynotesapp.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun scheduleBackup(context: Context) {

    WorkManager.getInstance(context).cancelAllWorkByTag("note_backup")
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED) //worker will run,Network is Connected.
        .build()


    val backupRequest = PeriodicWorkRequestBuilder<BackupWorker>(15, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .setInitialDelay(1, TimeUnit.MINUTES)
        .build()


    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            "note_backup",
            ExistingPeriodicWorkPolicy.UPDATE,
            backupRequest
        )
}