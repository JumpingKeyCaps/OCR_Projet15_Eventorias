package com.openclassroom.eventorias

import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassroom.eventorias.screen.main.eventCreation.EventCreationScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class EventCreationScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testEventTitleTextFieldIsEditable() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val composeView = ComposeView(activity).apply {
                    setContent {
                        EventCreationScreen(onBackClicked = {})
                    }
                }
                activity.setContentView(composeView)
            }
            composeTestRule
                .onNodeWithTag("EventCreationScreen_titleInput")
                .performTextInput("My Event Title")
            composeTestRule
                .onNodeWithText("My Event Title")
                .assertExists()
        }
    }

    @Test
    fun testDescriptionFieldIsEditable() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val composeView = ComposeView(activity).apply {
                    setContent {
                        EventCreationScreen(onBackClicked = {})
                    }
                }
                activity.setContentView(composeView)
            }
            composeTestRule
                .onNodeWithTag("EventCreationScreen_DescriptionInput")
                .performTextInput("My Event Description")
            composeTestRule
                .onNodeWithText("My Event Description")
                .assertExists()
        }
    }

    @Test
    fun testAddressFieldIsEditable() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val composeView = ComposeView(activity).apply {
                    setContent {
                        EventCreationScreen(onBackClicked = {})
                    }
                }
                activity.setContentView(composeView)
            }
            composeTestRule
                .onNodeWithTag("EventCreationScreen_AddressInput")
                .performTextInput("123 Main Street")
            composeTestRule
                .onNodeWithText("123 Main Street")
                .assertExists()
        }
    }

    @Test
    fun testDateFieldOpensDatePicker() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val composeView = ComposeView(activity).apply {
                    setContent {
                        EventCreationScreen(onBackClicked = {})
                    }
                }
                activity.setContentView(composeView)
            }
            composeTestRule
                .onNodeWithTag("EventCreationScreen_DateInput", useUnmergedTree = true)
                .assertExists()
                .performClick()
        }
    }

    @Test
    fun testDateFieldOpensTimePicker() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val composeView = ComposeView(activity).apply {
                    setContent {
                        EventCreationScreen(onBackClicked = {})
                    }
                }
                activity.setContentView(composeView)
            }
            composeTestRule
                .onNodeWithTag("EventCreationScreen_TimeInput", useUnmergedTree = true)
                .assertExists()
                .performClick()

        }
    }

    @Test
    fun testBackButtonNavigatesBack() {
        var backClicked = false
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val composeView = ComposeView(activity).apply {
                    setContent {
                        EventCreationScreen(onBackClicked = { backClicked = true })
                    }
                }
                activity.setContentView(composeView)
            }
            composeTestRule
                .onNodeWithTag("EventCreationScreen_BackButton") // Ajoutez un tag au bouton de retour
                .performClick()

            assert(backClicked) { "Back button action was not triggered." }
        }
    }

    @Test
    fun testCompleteEventCreationFlow() {

        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val composeView = ComposeView(activity).apply {
                    setContent {
                        EventCreationScreen(onBackClicked = {})
                    }
                }
                activity.setContentView(composeView)
            }

            composeTestRule.onNodeWithTag("EventCreationScreen_titleInput").performTextInput("Birthday Party")
            composeTestRule.onNodeWithTag("EventCreationScreen_DescriptionInput").performTextInput("Celebrate with friends")
            composeTestRule.onNodeWithTag("EventCreationScreen_AddressInput").performTextInput("123 Party Lane")
            composeTestRule.onNodeWithTag("EventCreationScreen_DateInput", useUnmergedTree = true).performClick()
            composeTestRule.onNodeWithTag("EventCreationScreen_SubmitButton").performClick()

        }
    }

    @Test
    fun testCameraButtonClick() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val composeView = ComposeView(activity).apply {
                    setContent {
                        EventCreationScreen(onBackClicked = {})
                    }
                }
                activity.setContentView(composeView)
            }
            composeTestRule.onNodeWithTag("EventCreationScreen_CameraButton").performClick()
        }
    }

    @Test
    fun testGalleryButtonClick() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val composeView = ComposeView(activity).apply {
                    setContent {
                        EventCreationScreen(onBackClicked = {})
                    }
                }
                activity.setContentView(composeView)
            }
            composeTestRule.onNodeWithTag("EventCreationScreen_GalleryButton").performClick()
        }
    }
}