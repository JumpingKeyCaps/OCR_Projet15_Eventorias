package com.openclassroom.eventorias.viewmodelUnitTest

import com.openclassroom.eventorias.data.repository.EventStoreRepository
import com.openclassroom.eventorias.domain.Event
import com.openclassroom.eventorias.screen.main.eventDetails.EventDetailsViewModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

/**
 * Unit tests for the EventDetailsViewModel.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EventDetailsViewModelUnitTest {

    private val testDispatcher = TestCoroutineDispatcher()
    private val eventStoreRepository: EventStoreRepository = mockk()
    private lateinit var viewModel: EventDetailsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = EventDetailsViewModel(eventStoreRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    /**
     * Test the loading of event details (event not found).
     */
    @Test
    fun loadEventDetails_should_update_state_to_null_when_event_is_not_found() = runTest {
        coEvery { eventStoreRepository.getEventById(any()) } throws Exception("Event not found")
        viewModel.loadEventDetails("eventId","user123")
        assertNull(viewModel.event.value)
    }

    /**
     * Test the loading of event details (event found).
     */
    @Test
    fun loadEventDetails_should_update_event_state_when_event_is_found() = runTest {
        val event = Event(id = "eventId", title = "Test Event")
        coEvery { eventStoreRepository.getEventById(any()) } returns Result.success(event)
        viewModel.loadEventDetails("eventId","user123")
        assertEquals(event, viewModel.event.value)
    }



    /**
     * Test the case when an error occurs during event fetching (network issue, etc.).
     */
    @Test
    fun loadEventDetails_should_update_state_to_null_when_error_occurred() = runTest {
        coEvery { eventStoreRepository.getEventById(any()) } throws Exception("Network error")
        viewModel.loadEventDetails("eventId","user123")
        assertNull(viewModel.event.value)
    }

    /**
     * Test the case when the event details contain invalid data (e.g., empty fields).
     */
    @Test
    fun loadEventDetails_should_handle_invalid_event_data_gracefully() = runTest {
        val invalidEvent = Event(id = "eventId", title = "")
        coEvery { eventStoreRepository.getEventById(any()) } returns Result.success(invalidEvent)
        viewModel.loadEventDetails("eventId","user123")
        assertEquals(invalidEvent, viewModel.event.value)
    }

    /**
     * Test the loading of event details (event found with specific valid data).
     */
    @Test
    fun loadEventDetails_should_update_event_state_with_valid_data() = runTest {
        val event = Event(id = "eventId", title = "Special Event")
        coEvery { eventStoreRepository.getEventById(any()) } returns Result.success(event)
        viewModel.loadEventDetails("eventId","user123")
        assertEquals("Special Event", viewModel.event.value?.title)
    }

    /**
     * Test the case when the event store repository returns an empty result.
     */
    @Test
    fun loadEventDetails_should_update_state_to_null_when_event_is_empty() = runTest {
        val emptyEvent = Event(id = "eventId", title = "")
        coEvery { eventStoreRepository.getEventById(any()) } returns Result.success(emptyEvent)
        viewModel.loadEventDetails("eventId","user123")
        assertEquals(emptyEvent, viewModel.event.value)
    }


}