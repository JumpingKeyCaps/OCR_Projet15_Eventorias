package com.openclassroom.eventorias.viewmodelUnitTest

import android.net.Uri
import com.google.firebase.auth.FirebaseUser
import com.openclassroom.eventorias.data.repository.AuthenticationRepository
import com.openclassroom.eventorias.data.repository.StorageRepository
import com.openclassroom.eventorias.data.repository.UserStoreRepository
import com.openclassroom.eventorias.domain.User
import com.openclassroom.eventorias.notification.NotificationPreferences
import com.openclassroom.eventorias.screen.main.userProfile.UserProfileViewModel
import io.mockk.coEvery
import io.mockk.coVerify
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
 * Unit tests for the UserProfileViewModel.
 */
@ExperimentalCoroutinesApi
class UserProfileViewModelUnitTest {
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var userStoreRepository: UserStoreRepository
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var storageRepository: StorageRepository
    private lateinit var preferences: NotificationPreferences
    private val testDispatcher = TestCoroutineDispatcher() // Dispatcher de test

    @Before
    fun setUp() {
        // Définir le dispatcher principal sur le dispatcher de test
        Dispatchers.setMain(testDispatcher)

        // Création du mock des repositories
        userStoreRepository = mockk()
        authenticationRepository = mockk()
        storageRepository = mockk()
        preferences = mockk()

        // Mocker le comportement de getNotificationsEnabled
        coEvery { preferences.getNotificationsEnabled() } returns true // ou false, selon votre besoin

        // Initialisation de la ViewModel avec les mocks
        viewModel = UserProfileViewModel(userStoreRepository, authenticationRepository, storageRepository, preferences)
    }

    /**
     * Test of the getUserProfile method.
     */
    @Test
    fun getUserProfile_should_call_userStoreRepository_getUser() = runTest {
        val userId = "mockUserId"
        val mockUser = mockk<User>()
        coEvery { userStoreRepository.getUser(userId) } returns mockUser
        viewModel.getUserProfile(userId)
        coVerify { userStoreRepository.getUser(userId) }
    }

    /**
     * Test of the signOutUser method.
     */
    @Test
    fun deleteUserAccount_should_call_removeUserAuth_and_deleteUser() = runTest {
        val mockUser = mockk<FirebaseUser>()
        val userId = "mockUserId"
        coEvery { mockUser.uid } returns userId // Mock l'ID utilisateur pour FirebaseUser
        coEvery { userStoreRepository.deleteUser(userId) } returns Unit
        coEvery { authenticationRepository.deleteUserAccount(mockUser) } returns flowOf(Result.success(Unit))

        // Appel de la méthode deleteUserAccount
        viewModel.deleteUserAccount(mockUser)

        // Vérification que deleteUser a été appelé avec le bon userId
        coVerify { userStoreRepository.deleteUser(userId) }
        // Vérification que deleteUserAccount a été appelé sur authenticationRepository
        coVerify { authenticationRepository.deleteUserAccount(mockUser) }
    }


    /**
     * Test of the uploadUserProfilePicture method.
     */
    @Test
    fun uploadUserProfilePicture_should_call_storageRepository_and_updateProfile() = runTest {
        val userId = "mockUserId"
        val mockUri = mockk<Uri>()
        val mockOldImageUrl = "oldImageUrl"
        val mockNewImageUrl = "newImageUrl"

        coEvery { userStoreRepository.getUserProfilePicture(userId) } returns mockOldImageUrl
        coEvery { storageRepository.deleteImage(mockOldImageUrl) } returns Result.success(Unit)
        coEvery { storageRepository.uploadUserImage(mockUri) } returns Result.success(mockNewImageUrl)
        coEvery { userStoreRepository.updateProfilePicture(userId, mockNewImageUrl) } returns Unit

        // Appel de la méthode uploadUserProfilePicture
        viewModel.uploadUserProfilePicture(userId, mockUri)

        // Vérification que les méthodes storage et updateProfile ont bien été appelées
        coVerify { storageRepository.deleteImage(mockOldImageUrl) }
        coVerify { storageRepository.uploadUserImage(mockUri) }
        coVerify { userStoreRepository.updateProfilePicture(userId, mockNewImageUrl) }
    }

    @After
    fun tearDown() {
        // Réinitialiser le dispatcher principal après les tests
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines() // Nettoyage des coroutines
    }
}