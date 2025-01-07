package com.openclassroom.eventorias.utils

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Extension function to format a time string from 24-hour to 12-hour format with AM/PM.
 * @return The formatted time string.
 */
fun String.toFormattedTime(): String {
    return try {

        // check if the time format is "HH:mm"
        if (!this.matches(Regex("^([01]?\\d|2[0-3]):[0-5]\\d$"))) {
            return this // Return the original string if it doesn't match the expected format
        }

        // Parse the time in the 24-hour format
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = inputFormat.parse(this)

        // Format to the 12-hour format with AM/PM
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        outputFormat.format(date ?: return this)

    } catch (e: Exception) {
        this // Return the original string in case of error
    }
}