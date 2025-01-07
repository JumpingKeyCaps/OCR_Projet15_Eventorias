package com.openclassroom.eventorias.utilsUnitTest

import com.openclassroom.eventorias.utils.toFormattedDate
import junit.framework.TestCase.assertEquals
import org.junit.Test

/**
 * Unit tests for the Date format converter extensions method.
 */
class DateExtensionsUnitTest {

    /**
     * Tests the toFormattedDate extension function.
     */
    @Test
    fun testToFormattedDate_ValidDate() {
        // Setup
        val inputDate = "12/31/2025"
        val expectedOutput = "Décembre 31, 2025"

        // Execution
        val formattedDate = inputDate.toFormattedDate()

        // Assertion
        assertEquals(expectedOutput, formattedDate)
    }

    /**
     * Tests the toFormattedDate extension function with an invalid date format.
     */
    @Test
    fun testToFormattedDate_InvalidDate() {
        // Setup
        val invalidDate = "invalid-date"

        // Execution
        val formattedDate = invalidDate.toFormattedDate()

        // Assertion
        assertEquals(invalidDate, formattedDate)
    }

    /**
     * Tests the toFormattedDate extension function with an empty string.
     */
    @Test
    fun testToFormattedDate_EmptyString() {
        // Setup
        val emptyString = ""

        // Execution
        val formattedDate = emptyString.toFormattedDate()

        // Assertion
        assertEquals(emptyString, formattedDate)
    }

    /**
     * Tests the toFormattedDate extension function with a null-like string.
     */
    @Test
    fun testToFormattedDate_NullLikeString() {
        // Setup
        val nullLikeString = "null"

        // Execution
        val formattedDate = nullLikeString.toFormattedDate()

        // Assertion
        assertEquals(nullLikeString, formattedDate)
    }
}