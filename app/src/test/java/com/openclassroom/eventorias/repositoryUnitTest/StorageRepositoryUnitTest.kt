package com.openclassroom.eventorias.repositoryUnitTest

import android.net.Uri
import com.openclassroom.eventorias.data.repository.StorageRepository
import com.openclassroom.eventorias.data.service.storage.FirebaseStorageService
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
 * Unit tests for the StorageRepository class.
 */
@ExperimentalCoroutinesApi
class StorageRepositoryUnitTest {

    private lateinit var storageRepository: StorageRepository
    private val firebaseStorageService: FirebaseStorageService = mockk()

    @Before
    fun setup() {
        storageRepository = StorageRepository(firebaseStorageService)
    }

    /**
     * Test the deleteImage method of the StorageRepository.
     */
    @Test
    fun testDeleteImageSuccess() = runTest {
        val imageUrl = "https://example.com/image.jpg"
        coEvery { firebaseStorageService.deleteImage(imageUrl) } just Runs

        val result = storageRepository.deleteImage(imageUrl)

        assertTrue(result.isSuccess)
        coVerify { firebaseStorageService.deleteImage(imageUrl) }
    }

    /**
     * Test the deleteImage method of the StorageRepository in case of failure.
     */
    @Test
    fun testDeleteImageFailure() = runTest {
        val imageUrl = "https://example.com/image.jpg"
        val exception = Exception("Deletion failed")
        coEvery { firebaseStorageService.deleteImage(imageUrl) } throws exception

        val result = storageRepository.deleteImage(imageUrl)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify { firebaseStorageService.deleteImage(imageUrl) }
    }

    /**
     * Test the uploadUserImage method of the StorageRepository.
     */
    @Test
    fun testUploadUserImageSuccess() = runTest {
        val imageUri = mockk<Uri>()
        val imageUrl = "https://example.com/user_image.jpg"
        coEvery { firebaseStorageService.uploadUserImage(imageUri) } returns imageUrl

        val result = storageRepository.uploadUserImage(imageUri)

        assertTrue(result.isSuccess)
        assertEquals(imageUrl, result.getOrNull())
        coVerify { firebaseStorageService.uploadUserImage(imageUri) }
    }

    /**
     * Test the uploadUserImage method of the StorageRepository in case of failure.
     */
    @Test
    fun testUploadUserImageFailure() = runTest {
        val imageUri = mockk<Uri>()
        val exception = Exception("Upload failed")
        coEvery { firebaseStorageService.uploadUserImage(imageUri) } throws exception

        val result = storageRepository.uploadUserImage(imageUri)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify { firebaseStorageService.uploadUserImage(imageUri) }
    }

    /**
     * Test the uploadEventImage method of the StorageRepository.
     */
    @Test
    fun testUploadEventImageSuccess() = runTest {
        val imageUri = mockk<Uri>()
        val imageUrl = "https://example.com/event_image.jpg"
        coEvery { firebaseStorageService.uploadEventImage(imageUri) } returns imageUrl

        val result = storageRepository.uploadEventImage(imageUri)

        assertTrue(result.isSuccess)
        assertEquals(imageUrl, result.getOrNull())
        coVerify { firebaseStorageService.uploadEventImage(imageUri) }
    }

    /**
     * Test the uploadEventImage method of the StorageRepository in case of failure.
     */
    @Test
    fun testUploadEventImageFailure() = runTest {
        val imageUri = mockk<Uri>()
        val exception = Exception("Upload failed")
        coEvery { firebaseStorageService.uploadEventImage(imageUri) } throws exception

        val result = storageRepository.uploadEventImage(imageUri)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify { firebaseStorageService.uploadEventImage(imageUri) }
    }
}