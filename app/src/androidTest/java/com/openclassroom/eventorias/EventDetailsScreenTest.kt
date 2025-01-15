package com.openclassroom.eventorias

import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassroom.eventorias.domain.Event
import com.openclassroom.eventorias.screen.main.eventDetails.EventDetailsScreen
import com.openclassroom.eventorias.screen.main.eventDetails.EventDetailsViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EventDetailsScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testEventDetailsScreen_loadsCorrectly() {
        val event = Event(
            id = "123",
            title = "Sample Event",
            description = "Event Description",
            date = "07/08/2025",
            time = "10:00",
            location = "Sample Location",
            pictureURL = "https://example.com/pic.jpg",
            authorPictureURL = "https://example.com/author.jpg",
            participants = listOf("User1", "User2")
        )

        val viewModel = mockk<EventDetailsViewModel>(relaxed = true)
        val eventFlow: Flow<Event> = flowOf(event)
        val stateFlow: StateFlow<Event?> = eventFlow.stateIn(scope = CoroutineScope(Dispatchers.Default), started = SharingStarted.Lazily, initialValue = null)
        coEvery { viewModel.event } returns stateFlow
        val userParticipationFlow: StateFlow<Boolean> = flowOf(true).stateIn(CoroutineScope(Dispatchers.Default), SharingStarted.Lazily, false)
        coEvery { viewModel.userParticipation } returns userParticipationFlow
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val composeView = ComposeView(activity).apply {
                    setContent {
                        EventDetailsScreen(
                            eventId = event.id,
                            onBackClicked = {},
                            viewmodel = viewModel
                        )
                    }
                }
                activity.setContentView(composeView)
            }

            composeTestRule
                .onNodeWithTag("EventDetails_Title")
                .assertTextEquals("Sample Event")
                .assertExists()

            composeTestRule
                .onNodeWithTag("EventDetails_Description")
                .assertTextEquals("Event Description")
                .assertExists()

            composeTestRule
                .onNodeWithTag("EventDetails_Date")
                .assertExists()

            composeTestRule
                .onNodeWithTag("EventDetails_Time")
                .assertExists()

            composeTestRule
                .onNodeWithTag("EventDetails_Location")
                .assertTextEquals("Sample Location")
                .assertExists()
        }
    }

    @Test
    fun eventDetailsScreen_backButtonTriggersNavigation() {
        val viewModel = mockk<EventDetailsViewModel>(relaxed = true)
        val event = Event(
            id = "123",
            title = "Sample Event",
            description = "Event Description",
            date = "07/08/2025",
            time = "10:00",
            location = "Sample Location",
            pictureURL = "https://example.com/pic.jpg",
            authorPictureURL = "https://example.com/author.jpg",
            participants = listOf("User1", "User2")
        )
        val eventFlow: Flow<Event?> = flowOf(event)
        val userParticipationFlow: Flow<Boolean> = flowOf(true)

        coEvery { viewModel.event } returns eventFlow.stateIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.Lazily,
            initialValue = null
        )
        coEvery { viewModel.userParticipation } returns userParticipationFlow.stateIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.Lazily,
            initialValue = false
        )

        var backClicked = false

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val composeView = ComposeView(activity).apply {
                    setContent {
                        EventDetailsScreen(
                            eventId = event.id,
                            onBackClicked = { backClicked = true },
                            viewmodel = viewModel
                        )
                    }
                }
                activity.setContentView(composeView)
            }
            composeTestRule.onNodeWithTag("Back_Button").performClick()
            assert(backClicked)
        }
    }

    @Test
    fun eventDetailsScreen_clickParticipateButton_callsViewModelMethod() {
        val viewModel = mockk<EventDetailsViewModel>(relaxed = true)
        val event = Event(
            id = "123",
            title = "Sample Event",
            description = "Event Description",
            date = "07/08/2025",
            time = "10:00",
            location = "Sample Location",
            pictureURL = "https://example.com/pic.jpg",
            authorPictureURL = "https://example.com/author.jpg",
            participants = listOf("User1", "User2")
        )
        val eventFlow: Flow<Event?> = flowOf(event)
        val userParticipationFlow: Flow<Boolean> = flowOf(false)

        coEvery { viewModel.event } returns eventFlow.stateIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.Lazily,
            initialValue = null
        )
        coEvery { viewModel.userParticipation } returns userParticipationFlow.stateIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.Lazily,
            initialValue = false
        )

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val composeView = ComposeView(activity).apply {
                    setContent {
                        EventDetailsScreen(
                            eventId = event.id,
                            onBackClicked = {},
                            viewmodel = viewModel
                        )
                    }
                }
                activity.setContentView(composeView)
            }
            composeTestRule.onNodeWithTag("Participate_Button").performClick()

        }
    }


    @After
    fun tearDown() {
    }
}