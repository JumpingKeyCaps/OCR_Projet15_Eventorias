package com.openclassroom.eventorias.repositoryUnitTest

import com.openclassroom.eventorias.data.repository.UserStoreRepository
import com.openclassroom.eventorias.data.service.firestore.FirestoreService
import com.openclassroom.eventorias.domain.User
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the UserStoreRepository class.
 */
@ExperimentalCoroutinesApi
class UserStoreRepositoryUnitTest {

    private lateinit var userStoreRepository: UserStoreRepository
    private val firestoreService: FirestoreService = mockk()

    @Before
    fun setup() {
        userStoreRepository = UserStoreRepository(firestoreService)
    }

    /**
     * Test the addUser method of the UserStoreRepository.
     */
    @Test
    fun testAddUser() = runTest {
        val user = User(id = "123", firstname = "Test User")
        coEvery { firestoreService.addUser(user) } just Runs

        userStoreRepository.addUser(user)

        coVerify { firestoreService.addUser(user) }
    }

    /**
     * Test the getUser method of the UserStoreRepository.
     */
    @Test
    fun testGetUserSuccess() = runTest {
        val userId = "123"
        val user = User(id = userId, firstname = "Test User")
        coEvery { firestoreService.getUserById(userId) } returns user

        val result = userStoreRepository.getUser(userId)

        assertEquals(user, result)
        coVerify { firestoreService.getUserById(userId) }
    }

    /**
     * Test the getUser method of the UserStoreRepository in case of failure.
     */
    @Test
    fun testGetUserFailure() = runTest {
        val userId = "123"
        val exception = Exception("User not found")
        coEvery { firestoreService.getUserById(userId) } throws exception

        val result = runCatching { userStoreRepository.getUser(userId) }

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify { firestoreService.getUserById(userId) }
    }

    /**
     * Test the deleteUser method of the UserStoreRepository.
     */
    @Test
    fun testDeleteUser() = runTest {
        val userId = "123"
        coEvery { firestoreService.deleteUser(userId) } just Runs

        userStoreRepository.deleteUser(userId)

        coVerify { firestoreService.deleteUser(userId) }
    }

    /**
     * Test the updateProfilePicture method of the UserStoreRepository.
     */
    @Test
    fun testUpdateProfilePicture() = runTest {
        val userId = "123"
        val profilePictureUrl = "https://example.com/profile.jpg"
        coEvery { firestoreService.updateUserProfilePicture(userId, profilePictureUrl) } just Runs

        userStoreRepository.updateProfilePicture(userId, profilePictureUrl)

        coVerify { firestoreService.updateUserProfilePicture(userId, profilePictureUrl) }
    }

    /**
     * Test the getUserProfilePicture method of the UserStoreRepository.
     */
    @Test
    fun testGetUserProfilePictureSuccess() = runTest {
        val userId = "123"
        val profilePictureUrl = "https://example.com/profile.jpg"
        coEvery { firestoreService.getUserProfilePicture(userId) } returns profilePictureUrl

        val result = userStoreRepository.getUserProfilePicture(userId)

        assertEquals(profilePictureUrl, result)
        coVerify { firestoreService.getUserProfilePicture(userId) }
    }

    /**
     * Test the getUserProfilePicture method of the UserStoreRepository in case of failure.
     */
    @Test
    fun testGetUserProfilePictureFailure() = runTest {
        val userId = "123"
        val exception = Exception("Profile picture not found")
        coEvery { firestoreService.getUserProfilePicture(userId) } throws exception

        val result = runCatching { userStoreRepository.getUserProfilePicture(userId) }

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify { firestoreService.getUserProfilePicture(userId) }
    }
}