package com.openclassroom.eventorias

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
class SignUpScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
        ActivityScenario.launch(MainActivity::class.java)
        composeTestRule.onNodeWithTag("AuthenticationScreen_emailSignInButton").performClick()
        composeTestRule.onNodeWithTag("AuthenticationScreen_bottomSheet").assertIsDisplayed()
        composeTestRule.onNodeWithTag("authentication_SignInScreen_GoSignUp_B").performClick()

    }

    @After
    fun tearDown() { }

    @Test
    fun testAllIsWellDisplayed() {
        composeTestRule.onNodeWithTag("SignUpScreen_header_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SignUpScreen_header_2").assertIsDisplayed()

        composeTestRule.onNodeWithTag("SignUpScreen_FirstNameInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SignUpScreen_LastNameInput").assertIsDisplayed()

        composeTestRule.onNodeWithTag("SignUpScreen_EmailInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SignUpScreen_PasswordInput").assertIsDisplayed()

    }

    @Test
    fun testAllInput() {
        composeTestRule.onNodeWithTag("SignUpScreen_FirstNameInput").performTextInput("John")
        composeTestRule.onNodeWithText("John").assertExists()
        composeTestRule.onNodeWithTag("SignUpScreen_LastNameInput").performTextInput("Doe")
        composeTestRule.onNodeWithText("Doe").assertExists()
        composeTestRule.onNodeWithTag("SignUpScreen_EmailInput").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("test@example.com").assertExists()
        composeTestRule.onNodeWithTag("SignUpScreen_PasswordInput").performTextInput("password123")
        composeTestRule.onNodeWithTag("SignUpScreen_PasswordInput").assertExists()
    }



    @Test
    fun testSignUpButtonIsDisplayed() {
        composeTestRule.onNodeWithTag("SignUpScreen_SignUpButton").assertIsDisplayed()
    }

    @Test
    fun testSignUpButtonClick() {
        composeTestRule.onNodeWithTag("SignUpScreen_SignUpButton").performClick()
    }

    @Test
    fun testGoSignInLink() {
        composeTestRule.onNodeWithTag("SignUpScreen_GoSignIn_A").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SignUpScreen_GoSignIn_B").assertIsDisplayed()
        composeTestRule.onNodeWithTag("SignUpScreen_GoSignIn_B").performClick()
    }
}