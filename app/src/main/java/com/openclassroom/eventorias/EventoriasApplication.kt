package com.openclassroom.eventorias

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

/**
 *  The application class is the entry point into the application process used for global application-level
 *  initialization tasks such as dependency injection setup using Hilt.
 */
@HiltAndroidApp
class EventoriasApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }

}