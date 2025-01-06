package com.openclassroom.eventorias.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.openclassroom.eventorias.MainActivity
import com.openclassroom.eventorias.R

/**
 * Firebase Cloud Messaging (FCM) service class used to notify the user
 */
class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)


        // Créer un intent pour ouvrir l'activité principale
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Créer un PendingIntent
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0, // Identifiant unique pour ce PendingIntent
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, "default_channel")
            .setContentTitle(remoteMessage.notification?.title ?: "Notification")
            .setContentText(remoteMessage.notification?.body ?: "Message")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)  // Associer le PendingIntent
            .setAutoCancel(true)
            .build()


        Log.d("FCM", "notification Message received")

        notificationManager.notify(0, notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Enregistrer le token dans Firestore ou backend
        Log.d("FCM", "New token: $token")

    }
}