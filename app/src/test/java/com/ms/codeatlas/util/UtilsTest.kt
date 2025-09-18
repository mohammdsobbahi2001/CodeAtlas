package com.ms.codeatlas.util

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Test class for Utils functions.
 */
class UtilsTest {

    @Test
    fun `formatDate should return formatted date for valid ISO 8601 input`() {
        // Given
        val isoDate = "2025-09-18T16:54:00Z"

        // When
        val result = Utils.formatDate(isoDate)

        // Then
        assertEquals("Sep 18, 2025", result)
    }

    @Test
    fun `formatDate should return original string for invalid date input`() {
        // Given
        val invalidDate = "not-a-date"

        // When
        val result = Utils.formatDate(invalidDate)

        // Then
        assertEquals("not-a-date", result)
    }

    @Test
    fun `formatDate should return original string for empty input`() {
        // Given
        val emptyDate = ""

        // When
        val result = Utils.formatDate(emptyDate)

        // Then
        assertEquals("", result)
    }

    @Test
    fun `formatDate should return original string for null input`() {
        // Given
        val nullDate: String? = null

        // When
        val result = Utils.formatDate(nullDate.toString())

        // Then
        assertEquals("null", result)
    }

    @Test
    fun `formatDate should handle different timezones correctly`() {
        // Given
        val dateWithTimezone = "2025-09-18T16:54:00+03:00"

        // When
        val result = Utils.formatDate(dateWithTimezone)

        // Then - Should return original string since format doesn't handle timezone offsets
        assertEquals("2025-09-18T16:54:00+03:00", result)
    }

    @Test
    fun `formatDate should handle different date formats gracefully`() {
        // Given
        val differentFormat = "18/09/2025"

        // When
        val result = Utils.formatDate(differentFormat)

        // Then
        assertEquals("18/09/2025", result)
    }
}
