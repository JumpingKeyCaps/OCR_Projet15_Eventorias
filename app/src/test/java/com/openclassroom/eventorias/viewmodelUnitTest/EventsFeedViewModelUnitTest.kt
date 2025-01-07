package com.openclassroom.eventorias.viewmodelUnitTest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openclassroom.eventorias.data.repository.EventStoreRepository
import com.openclassroom.eventorias.domain.Event
import com.openclassroom.eventorias.screen.main.eventsFeed.EventsFeedState
import com.openclassroom.eventorias.screen.main.eventsFeed.EventsFeedViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

/**
 * Unit tests for the EventsFeedViewModel.
 */
@ExperimentalCoroutinesApi
class EventsFeedViewModelUnitTest {
    private val testDispatcher = TestCoroutineDispatcher()
    private val eventStoreRepository: EventStoreRepository = mockk()
    private lateinit var viewModel: EventsFeedViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = EventsFeedViewModel(eventStoreRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }



    /**
     * Test when there is an error fetching events.
     */
    @Test
    fun observeEvents_should_set_error_when_event_fetching_fails() = runTest {
        coEvery { eventStoreRepository.getEventsFlow() } returns flow { throw Exception("Error fetching events") }
        viewModel.observeEvents()
        assertTrue(viewModel.eventsState.value is EventsFeedState.Error)
        val error = (viewModel.eventsState.value as EventsFeedState.Error).message
        assertEquals("Unable to load events. Please try again.", error)
    }



    /**
     * Test when no events are found.
     */
    @Test
    fun observeEvents_should_set_error_when_no_events_found() = runTest {
        // Simulate no events
        coEvery { eventStoreRepository.getEventsFlow() } returns flowOf(emptyList())

        // Call observeEvents method
        viewModel.observeEvents()

        // Verify UI state is error with the correct message
        assertTrue(viewModel.eventsState.value is EventsFeedState.Error)
        val error = (viewModel.eventsState.value as EventsFeedState.Error).message
        assertEquals("No events found.", error)
    }




}