package com.openclassroom.eventorias.viewmodelUnitTest

import com.google.firebase.auth.FirebaseUser
import com.openclassroom.eventorias.data.repository.AuthenticationRepository
import com.openclassroom.eventorias.data.repository.UserStoreRepository
import com.openclassroom.eventorias.domain.User
import com.openclassroom.eventorias.screen.auth.AuthenticationViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the AuthenticationViewModel.
 */
@ExperimentalCoroutinesApi
class AuthenticationViewModelUnitTest {
    private lateinit var viewModel: AuthenticationViewModel
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var userStoreRepository: UserStoreRepository
    private val testDispatcher = TestCoroutineDispatcher() // Dispatcher de test

    @Before
    fun setUp() {
        // Définir le dispatcher principal sur le dispatcher de test
        Dispatchers.setMain(testDispatcher)

        // Création du mock du repository
        authenticationRepository = mockk()
        userStoreRepository = mockk()

        // Initialisation de la ViewModel avec le mock
        viewModel = AuthenticationViewModel(authenticationRepository, userStoreRepository)
    }

    /**
     * Test sign up user method.
     */
    @Test
    fun signUpUser_should_call_registerUser() = runTest {
        // Mock du retour de la méthode registerUser
        val mockUser = mockk<FirebaseUser>()
        val mockUid = "mockUid"  // L'ID utilisateur que vous souhaitez renvoyer
        val mockEmail = "test@example.com"  // L'email que vous souhaitez renvoyer

        coEvery { authenticationRepository.registerUser(any(), any()) } returns flowOf(Result.success(mockUser))
        every { mockUser.uid } returns mockUid  // Configuration pour getUid()
        every { mockUser.email } returns mockEmail  // Configuration pour getEmail()

        // Appel de la méthode signUpUser dans la ViewModel
        viewModel.signUpUser("test@example.com", "password123", "John", "Doe")

        // Vérification que registerUser a bien été appelé avec les bons arguments
        coVerify { authenticationRepository.registerUser("test@example.com", "password123") }
    }

    /**
     * Test the addUserProfileEntryInDatabase method behavior.
     */
    @Test
    fun addUserProfileEntryInDatabase_should_call_addUser() = runTest {
        // Mock de l'utilisateur
        val mockUser = mockk<FirebaseUser>()
        val mockUid = "mockUid"
        val mockEmail = "test@example.com"

        coEvery { authenticationRepository.registerUser(any(), any()) } returns flowOf(Result.success(mockUser))
        every { mockUser.uid } returns mockUid
        every { mockUser.email } returns mockEmail

        // Mock du repository UserStoreRepository
        coEvery { userStoreRepository.addUser(any()) } returns Unit  // On s'assure que cette méthode ne lance pas d'exception

        // Appel de la méthode signUpUser dans la ViewModel
        viewModel.signUpUser("test@example.com", "password123", "John", "Doe")

        // Vérification que la méthode addUser a été appelée avec le bon utilisateur formaté
        val formattedUser = User(mockUid, "John", "Doe", mockEmail)
        coVerify { userStoreRepository.addUser(formattedUser.copy(firstname = "John", lastname = "Doe")) }
    }


    /**
     * Test the signInUser method behavior.
     */
    @Test
    fun signInUser_should_call_signInUser() = runTest {
        // Mock du retour de la méthode signInUser
        val mockUser = mockk<FirebaseUser>()
        val mockUid = "mockUid"
        val mockEmail = "test@example.com"

        coEvery { authenticationRepository.signInUser(any(), any()) } returns flowOf(Result.success(mockUser))
        every { mockUser.uid } returns mockUid
        every { mockUser.email } returns mockEmail

        // Appel de la méthode signInUser dans la ViewModel
        viewModel.signInUser("test@example.com", "password123")

        // Vérification que signInUser a bien été appelé avec les bons arguments
        coVerify { authenticationRepository.signInUser("test@example.com", "password123") }
    }


    /**
     * Test the sendPasswordResetEmail method behavior.
     */
    @Test
    fun sendPasswordResetEmail_should_call_sendPasswordResetEmail() = runTest {
        // Mock du repository
        coEvery { authenticationRepository.sendPasswordResetEmail(any()) } returns flowOf(Result.success(Unit))

        // Appel de la méthode sendPasswordResetEmail dans la ViewModel
        viewModel.sendPasswordResetEmail("test@example.com")

        // Vérification que sendPasswordResetEmail a bien été appelé avec l'email
        coVerify { authenticationRepository.sendPasswordResetEmail("test@example.com") }
    }



    @After
    fun tearDown() {
        // Réinitialiser le dispatcher principal après les tests
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines() // Nettoyage des coroutines
    }


}