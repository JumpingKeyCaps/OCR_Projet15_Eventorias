package com.openclassroom.eventorias.domainUnitTest

import com.openclassroom.eventorias.domain.Event
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test

/**
 * Unit tests for the Event data model.
 */
class EventUnitTest {
    /**
     * Test the default constructor of the Event data model.
     */
    @Test
    fun testDefaultEventConstructor() {
        val event = Event()
        assertEquals("", event.id)
        assertEquals("", event.title)
        assertEquals("", event.description)
        assertEquals("", event.date)
        assertEquals("", event.time)
        assertEquals("", event.location)
        assertNull(event.pictureURL)
        assertTrue(event.participants.isEmpty())
        assertEquals("", event.authorId)
        assertNull(event.authorPictureURL)
    }

    /**
     * Test the custom constructor of the Event data model.
     */
    @Test
    fun testCustomEventConstructor() {
        val customEvent = Event(
            id = "1",
            title = "Test Event",
            description = "This is a test event",
            date = "2025-01-01",
            time = "10:00 AM",
            location = "22 Emerald street,New York",
            pictureURL = "http://test.com/pic.jpg",
            participants = listOf("user1", "user2"),
            authorId = "author1",
            authorPictureURL = "http://test.com/author.jpg"
        )

        assertEquals("1", customEvent.id)
        assertEquals("Test Event", customEvent.title)
        assertEquals("This is a test event", customEvent.description)
        assertEquals("2025-01-01", customEvent.date)
        assertEquals("10:00 AM", customEvent.time)
        assertEquals("22 Emerald street,New York", customEvent.location)
        assertEquals("http://test.com/pic.jpg", customEvent.pictureURL)
        assertEquals(listOf("user1", "user2"), customEvent.participants)
        assertEquals("author1", customEvent.authorId)
        assertEquals("http://test.com/author.jpg", customEvent.authorPictureURL)
    }


    /**
     * Test the equality of two events.
     */
    @Test
    fun testEventEquality() {
        val event1 = Event(
            id = "1",
            title = "Test Event",
            description = "This is a test event",
            date = "2025-01-01",
            time = "10:00 AM",
            location = "New York",
            pictureURL = "http://example.com/pic.jpg",
            participants = listOf("user1", "user2"),
            authorId = "author1",
            authorPictureURL = "http://example.com/author.jpg"
        )

        val event2 = Event(
            id = "1",
            title = "Test Event",
            description = "This is a test event",
            date = "2025-01-01",
            time = "10:00 AM",
            location = "New York",
            pictureURL = "http://example.com/pic.jpg",
            participants = listOf("user1", "user2"),
            authorId = "author1",
            authorPictureURL = "http://example.com/author.jpg"
        )

        assertEquals(event1, event2)
    }

    /**
     *  Test the null optional fields of the event
     */
    @Test
    fun testNullOptionalFields() {
        val event = Event(
            id = "2",
            title = "Another Event",
            description = "Description here",
            date = "2025-02-01",
            time = "2:00 PM",
            location = "Paris",
            pictureURL = null,
            participants = emptyList(),
            authorId = "author2",
            authorPictureURL = null
        )

        assertNull(event.pictureURL)
        assertNull(event.authorPictureURL)
    }

    /**
     * Test the immutability of the event
     */
    @Test
    fun testEventImmutability() {
        val event = Event(id = "1", title = "Test Event")
        assertEquals("1", event.id)
        assertEquals("Test Event", event.title)
    }



}