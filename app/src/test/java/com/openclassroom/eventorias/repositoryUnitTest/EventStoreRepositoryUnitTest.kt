package com.openclassroom.eventorias.repositoryUnitTest

import com.openclassroom.eventorias.data.repository.EventStoreRepository
import com.openclassroom.eventorias.data.service.firestore.FirestoreService
import com.openclassroom.eventorias.domain.Event
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the EventStoreRepository class.
 */
@ExperimentalCoroutinesApi
class EventStoreRepositoryUnitTest {

    private lateinit var firestoreService: FirestoreService
    private lateinit var eventStoreRepository: EventStoreRepository

    @Before
    fun setUp() {
        // Mock de FirestoreService
        firestoreService = mockk()
        eventStoreRepository = EventStoreRepository(firestoreService)
    }

    /**
     * Test the saveEvent method of the EventStoreRepository.
     */
    @Test
    fun testSaveEvent() = runTest {
        // Setup du mock pour addEvent
        val event = Event(id = "1", title = "Test Event", description = "Description")
        coEvery { firestoreService.addEvent(event) } just Runs

        // Exécution de la méthode
        val result = eventStoreRepository.saveEvent(event)

        // Vérification du résultat
        assertTrue(result.isSuccess)
        coVerify { firestoreService.addEvent(event) }
    }

    /**
     * Test the getEventsFlow method of the EventStoreRepository.
     */
    @Test
    fun testGetEventsFlow() = runTest {
        // Setup du mock pour fetchEventsAsFlow
        val mockEvents = listOf(Event(id = "1", title = "Test Event"))
        coEvery { firestoreService.fetchEventsAsFlow() } returns flowOf(mockEvents)

        // Exécution de la méthode
        val events = eventStoreRepository.getEventsFlow().first()

        // Vérification du résultat
        assertEquals(mockEvents, events)
        coVerify { firestoreService.fetchEventsAsFlow() }
    }

    /**
     * Test the getEventById method of the EventStoreRepository.
     */
    @Test
    fun testGetEventById() = runTest {
        // Setup du mock pour getEventById
        val eventId = "1"
        val mockEvent = Event(id = eventId, title = "Test Event")
        coEvery { firestoreService.getEventById(eventId) } returns mockEvent

        // Exécution de la méthode
        val result = eventStoreRepository.getEventById(eventId)

        // Vérification du résultat
        assertTrue(result.isSuccess)
        assertEquals(mockEvent, result.getOrNull())
        coVerify { firestoreService.getEventById(eventId) }
    }

    /**
     * Test the deleteEvent method of the EventStoreRepository.
     */
    @Test
    fun testDeleteEvent() = runTest {
        // Setup du mock pour deleteEvent
        val eventId = "1"
        coEvery { firestoreService.deleteEvent(eventId) } just Runs

        // Exécution de la méthode
        val result = eventStoreRepository.deleteEvent(eventId)

        // Vérification du résultat
        assertTrue(result.isSuccess)
        coVerify { firestoreService.deleteEvent(eventId) }
    }

    /**
     * Test the addParticipant method of the EventStoreRepository.
     */
    @Test
    fun testAddParticipant() = runTest {
        // Setup du mock pour addParticipant
        val eventId = "1"
        val userId = "user1"
        coEvery { firestoreService.addParticipant(eventId, userId) } just Runs

        // Exécution de la méthode
        val result = eventStoreRepository.addParticipant(eventId, userId)

        // Vérification du résultat
        assertTrue(result.isSuccess)
        coVerify { firestoreService.addParticipant(eventId, userId) }
    }

    /**
     * Test the removeParticipant method of the EventStoreRepository.
     */
    @Test
    fun testRemoveParticipant() = runTest {
        // Setup du mock pour removeParticipant
        val eventId = "1"
        val userId = "user1"
        coEvery { firestoreService.removeParticipant(eventId, userId) } just Runs

        // Exécution de la méthode
        val result = eventStoreRepository.removeParticipant(eventId, userId)

        // Vérification du résultat
        assertTrue(result.isSuccess)
        coVerify { firestoreService.removeParticipant(eventId, userId) }
    }




}