package com.openclassroom.eventorias

import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.openclassroom.eventorias.screen.main.MainScreen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
    }


    @Test
    fun mainScreen_bottomNavigation_isDisplayed() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val composeView = ComposeView(activity).apply {
                    setContent {
                        MainScreen(
                            onLogOutAction = {},
                            onCreateEventClicked = {},
                            onEventClicked = {}
                        )
                    }
                }
                activity.setContentView(composeView)
            }
            composeTestRule.onNodeWithTag("BottomNavigationBar")
                .assertIsDisplayed()
        }
    }

    @Test
    fun mainScreen_bottomNavigation_startsOnEventsFeedScreen() {
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                val composeView = ComposeView(activity).apply {
                    setContent {
                        MainScreen(
                            onLogOutAction = {},
                            onCreateEventClicked = {},
                            onEventClicked = {}
                        )
                    }
                }
                activity.setContentView(composeView)
            }
            composeTestRule.onNodeWithTag("NavigationBarItem_EventsFeed")
                .assertIsSelected()
        }
    }



}