package com.openclassroom.eventorias.viewmodelUnitTest

import com.openclassroom.eventorias.data.repository.EventStoreRepository
import com.openclassroom.eventorias.screen.main.eventsFeed.EventsFeedViewModel
import com.openclassroom.eventorias.screen.main.eventsFeed.SortOption
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

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
        every { eventStoreRepository.getEventsFlow() } returns flowOf(emptyList())
        viewModel = EventsFeedViewModel(eventStoreRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }


    @Test
    fun test_updateSortOption_should_update_the_sort_option() {
        // Arrange
        val newSortOption = SortOption.Participate

        // Act
        viewModel.updateSortOption(newSortOption, null)

        // Assert
        assertEquals(newSortOption, viewModel.sortOption.value)
    }

    @Test
    fun test_setDateSortingType_should_update_the_date_sorting_type() {
        // Arrange
        val newSortingType = true // Ascending order

        // Act
        viewModel.setDateSortingType(newSortingType)

        // Assert
        assertEquals(newSortingType, viewModel.dateSortingType.value)
    }





}