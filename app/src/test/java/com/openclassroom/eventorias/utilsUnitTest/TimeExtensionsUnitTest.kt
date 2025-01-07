package com.openclassroom.eventorias.utilsUnitTest

import com.openclassroom.eventorias.utils.toFormattedTime
import junit.framework.TestCase.assertEquals
import org.junit.Test

/**
 * Unit tests for the Time format converter extensions method.
 */
class TimeExtensionsUnitTest {
    /**
     * Tests the conversion of a valid 24-hour format time to 12-hour format with PM.
     */
    @Test
    fun testToFormattedTime_ValidTime() {
        // Setup
        val inputTime = "13:45"
        val expectedOutput = "01:45 PM" // 24h to 12h format with PM

        // Execution
        val formattedTime = inputTime.toFormattedTime()

        // Assertion
        assertEquals(expectedOutput, formattedTime)
    }

    /**
     * Tests the conversion of a valid 24-hour format time in the morning to 12-hour format with AM.
     */
    @Test
    fun testToFormattedTime_ValidMorningTime() {
        // Setup
        val inputTime = "08:15"
        val expectedOutput = "08:15 AM" // 24h to 12h format with AM

        // Execution
        val formattedTime = inputTime.toFormattedTime()

        // Assertion
        assertEquals(expectedOutput, formattedTime)
    }

    /**
     * Tests the conversion of midnight (00:00) in 24-hour format to 12-hour format with AM.
     */
    @Test
    fun testToFormattedTime_Midnight() {
        // Setup
        val inputTime = "00:00"
        val expectedOutput = "12:00 AM" // Midnight in 12-hour format

        // Execution
        val formattedTime = inputTime.toFormattedTime()

        // Assertion
        assertEquals(expectedOutput, formattedTime)
    }

    /**
     * Tests the conversion of noon (12:00) in 24-hour format to 12-hour format with PM.
     */
    @Test
    fun testToFormattedTime_Noon() {
        // Setup
        val inputTime = "12:00"
        val expectedOutput = "12:00 PM" // Noon in 12-hour format

        // Execution
        val formattedTime = inputTime.toFormattedTime()

        // Assertion
        assertEquals(expectedOutput, formattedTime)
    }

    /**
     * Tests the behavior of the function when given an invalid time string.
     * It should return the original input string.
     */
    @Test
    fun testToFormattedTime_InvalidTime() {
        // Setup
        val inputTime = "25:61" // Invalid time
        val expectedOutput = inputTime // Should return the original input

        // Execution
        val formattedTime = inputTime.toFormattedTime()

        // Assertion
        assertEquals(expectedOutput, formattedTime)
    }

    /**
     * Tests the behavior of the function when given an empty string.
     * It should return the original input string.
     */
    @Test
    fun testToFormattedTime_EmptyString() {
        // Setup
        val inputTime = "" // Empty input
        val expectedOutput = inputTime // Should return the original input

        // Execution
        val formattedTime = inputTime.toFormattedTime()

        // Assertion
        assertEquals(expectedOutput, formattedTime)
    }

}