package com.openclassroom.eventorias.utils

import java.text.SimpleDateFormat
import java.util.Locale


/**
 * Extension function to format a date string from MM/dd/yyyy to MMMM d, yyyy.
 * @return The formatted date string.
 */
fun String.toFormattedDate(): String {
    return try {
        // get the date from the MM/dd/yyyy format
        val inputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val date = inputFormat.parse(this)

        // Convert to the target format
        val outputFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val formattedDate = outputFormat.format(date ?: return this)

        // Set the 1st letter in Uppercase
        formattedDate.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    } catch (e: Exception) {
        this // in case of error, return the original string
    }
}