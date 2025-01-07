package com.openclassroom.eventorias.repositoryUnitTest

import com.google.firebase.auth.FirebaseUser
import com.openclassroom.eventorias.data.repository.AuthenticationRepository
import com.openclassroom.eventorias.data.service.authentication.FirebaseAuthService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the AuthenticationRepository class.
 */
@ExperimentalCoroutinesApi
class AuthenticationRepositoryUnitTest {

    private lateinit var authService: FirebaseAuthService
    private lateinit var authRepository: AuthenticationRepository

    @Before
    fun setUp() {
        authService = mockk()
        authRepository = AuthenticationRepository(authService)
    }


    /**
     * Test the registerUser method of the AuthenticationRepository.
     */
    @Test
    fun testRegisterUser() = runTest {
        // Setup du mock pour registerUser
        val email = "test@example.com"
        val password = "password123"
        val mockResult = Result.success(null)  // Succès de l'enregistrement
        coEvery { authService.registerUser(email, password) } returns mockResult

        // Exécution de la méthode
        val result = authRepository.registerUser(email, password).first()

        // Vérification du résultat
        assertTrue(result.isSuccess)
        coVerify { authService.registerUser(email, password) }
    }


    /**
     * Test the signInUser method of the AuthenticationRepository.
     */
    @Test
    fun testSignInUser() = runTest {
        // Setup du mock pour signInUser
        val email = "test@example.com"
        val password = "password123"
        val mockResult = Result.success(mockk<FirebaseUser>())  // Succès de la connexion
        coEvery { authService.signInUser(email, password) } returns mockResult

        // Exécution de la méthode
        val result = authRepository.signInUser(email, password).first()

        // Vérification du résultat
        assertTrue(result.isSuccess)
        coVerify { authService.signInUser(email, password) }
    }


    /**
     * Test the signOutUser method of the AuthenticationRepository.
     */
    @Test
    fun testSignOutUser() = runTest {
        // Setup du mock pour signOutUser
        val mockResult = Result.success(Unit)  // Succès de la déconnexion
        coEvery { authService.signOutUser() } returns mockResult

        // Exécution de la méthode
        val result = authRepository.signOutUser().first()

        // Vérification du résultat
        assertTrue(result.isSuccess)
        coVerify { authService.signOutUser() }
    }

    /**
     * Test the deleteUserAccount method of the AuthenticationRepository.
     */
    @Test
    fun testDeleteUserAccount() = runTest {
        // Setup du mock pour deleteUserAccount
        val mockUser = mockk<FirebaseUser>()
        val mockResult = Result.success(Unit)  // Succès de la suppression
        coEvery { authService.deleteUserAccount(mockUser) } returns mockResult

        // Exécution de la méthode
        val result = authRepository.deleteUserAccount(mockUser).first()

        // Vérification du résultat
        assertTrue(result.isSuccess)
        coVerify { authService.deleteUserAccount(mockUser) }
    }

    /**
     * Test the sendPasswordResetEmail method of the AuthenticationRepository.
     */
    @Test
    fun testSendPasswordResetEmail() = runTest {
        // Setup du mock pour sendPasswordResetEmail
        val email = "test@example.com"
        val mockResult = Result.success(Unit)  // Succès de l'envoi
        coEvery { authService.sendPasswordResetEmail(email) } returns mockResult

        // Exécution de la méthode
        val result = authRepository.sendPasswordResetEmail(email).first()

        // Vérification du résultat
        assertTrue(result.isSuccess)
        coVerify { authService.sendPasswordResetEmail(email) }
    }



}