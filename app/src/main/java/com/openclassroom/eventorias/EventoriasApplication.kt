package com.openclassroom.eventorias

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

/**
 *  The application class is the entry point into the application process used for global application-level
 *  initialization tasks such as dependency injection setup using Hilt.
 */
@HiltAndroidApp
class EventoriasApplication : Application(){

    /**
     * onCreate method of the application.
     *  - initializes Firebase.
     *  - creates a notification channel for API 26+.
     */
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        // Initialize notification channel (API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default_channel",
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

}