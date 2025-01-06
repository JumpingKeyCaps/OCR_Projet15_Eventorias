package com.openclassroom.eventorias.notification

import android.content.Context
import android.content.SharedPreferences

/**
 * NotificationPreferences class to manage user preferences related to notifications.
 * @param context The application context.
 */
class NotificationPreferences(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val NOTIFICATIONS_ENABLED_KEY = "notifications_enabled"
    }

    /**
     * Check if notifications are enabled for the user.
     * @return true if notifications are enabled, false otherwise.
     */
    fun getNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(NOTIFICATIONS_ENABLED_KEY, false) // Par défaut à false
    }

    /**
     * Set the user's preference to enable or disable notifications.
     * @param enabled true to enable notifications, false to disable them.
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(NOTIFICATIONS_ENABLED_KEY, enabled).apply()
    }
}