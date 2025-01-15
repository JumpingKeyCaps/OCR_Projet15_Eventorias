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
class SignInScreenTest {

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
    }

    @After
    fun tearDown() {

    }

    @Test
    fun testAllIsWellDisplayed() {
        composeTestRule.onNodeWithTag("authentication_SignInScreen_header_1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("authentication_SignInScreen_header_2").assertIsDisplayed()
        composeTestRule.onNodeWithTag("authentication_SignInScreen_EmailInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("authentication_SignInScreen_PasswordInput").assertIsDisplayed()
        composeTestRule.onNodeWithTag("authentication_SignInScreen_SignInButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("authentication_SignInScreen_GoSignUp_A").assertIsDisplayed()
        composeTestRule.onNodeWithTag("authentication_SignInScreen_GoSignUp_B").assertIsDisplayed()
    }

    @Test
    fun testAllInput() {
        composeTestRule.onNodeWithTag("authentication_SignInScreen_EmailInput").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("test@example.com").assertExists()

        composeTestRule.onNodeWithTag("authentication_SignInScreen_PasswordInput").performTextInput("password123")
        composeTestRule.onNodeWithTag("authentication_SignInScreen_PasswordInput").assertExists()

    }

    @Test
    fun testSignInButtonClick() {
        composeTestRule.onNodeWithTag("authentication_SignInScreen_SignInButton").performClick()
    }

    @Test
    fun testForgotPasswordLink() {
        composeTestRule.onNodeWithTag("authentication_SignInScreen_ForgotPassword").assertIsDisplayed()
        composeTestRule.onNodeWithTag("authentication_SignInScreen_ForgotPassword").performClick()
    }

    @Test
    fun testGoSignUpLink() {
        composeTestRule.onNodeWithTag("authentication_SignInScreen_GoSignUp_B").performClick()
    }
}