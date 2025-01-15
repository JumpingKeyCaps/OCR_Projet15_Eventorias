package com.openclassroom.eventorias

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AuthenticationScreenTest {

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

    // Test 1 : Vérification de la fonction du bouton de connexion Google
    @Test
    fun testGoogleSignInButton() {
        ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.onNodeWithTag("AuthenticationScreen_googleSignInButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("AuthenticationScreen_googleSignInButton").performClick()
    }

    // Test 2 : Vérification de l'affichage du Bottom Sheet lors du clic sur le bouton de connexion par email
    @Test
    fun testEmailSignInButton() {
        ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.onNodeWithTag("AuthenticationScreen_emailSignInButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("AuthenticationScreen_emailSignInButton").performClick()
        composeTestRule.onNodeWithTag("AuthenticationScreen_bottomSheet").assertIsDisplayed()
    }

}