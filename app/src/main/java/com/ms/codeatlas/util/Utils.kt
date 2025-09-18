package com.ms.codeatlas.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility functions for formatting and general purposes.
 */
object Utils {

    /**
     * Formats a date string from ISO 8601 format to a readable format.
     *
     * Example:
     * Input: "2025-09-18T16:54:00Z"
     * Output: "Sep 18, 2025"
     *
     * @param dateString The ISO 8601 date string to format.
     * @return The formatted date string, or the original string if parsing fails.
     */
    fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (_: Exception) {
            dateString
        }
    }

}
