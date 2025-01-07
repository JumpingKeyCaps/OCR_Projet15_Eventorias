package com.openclassroom.eventorias.viewmodelUnitTest

import android.net.Uri
import com.openclassroom.eventorias.data.repository.EventStoreRepository
import com.openclassroom.eventorias.data.repository.StorageRepository
import com.openclassroom.eventorias.screen.main.eventCreation.EventCreationViewModel
import com.openclassroom.eventorias.screen.main.eventCreation.UiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the EventCreationViewModel.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EventCreationViewModelUnitTest {
    private val testDispatcher = TestCoroutineDispatcher()
    private val storageRepository: StorageRepository = mockk()
    private val eventStoreRepository: EventStoreRepository = mockk()
    private lateinit var viewModel: EventCreationViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = EventCreationViewModel(storageRepository, eventStoreRepository)
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    /**
     * Test the event creation when the user is not authenticated.
     */
    @Test
    fun saveEvent_should_set_error_when_user_is_not_authenticated() = runTest {
        // Simulate unauthenticated user (authorId is null)
        val authorId: String? = null
        viewModel.saveEvent("Event Title", "Event Description", "2025-01-01", "12:00", "Event Location", null, authorId)

        // Check that the UI state has an error with the correct message
        assertTrue(viewModel.uiState.value is UiState.Error)
        val error = (viewModel.uiState.value as UiState.Error).message
        assertEquals("User not authenticated", error)
    }

    /**
     * Test the event creation with missing required fields.
     */
    @Test
    fun saveEvent_should_set_error_when_required_fields_are_missing() = runTest {
        val authorId = "author123"
        viewModel.saveEvent("", "Event Description", "", "12:00", "", null, authorId)

        // Check that the UI state has an error with the validation message
        assertTrue(viewModel.uiState.value is UiState.Error)
        val error = (viewModel.uiState.value as UiState.Error).message
        assertEquals("Title cannot be empty", error)
    }

    /**
     * Test the event creation when image is uploaded successfully.
     */
    @Test
    fun saveEvent_should_create_event_and_upload_image() = runTest {
        val authorId = "author123"
        val imageUri = mockk<Uri>(relaxed = true) // Créez un mock pour Uri
        val pictureUrl = "http://example.com/event_image.jpg"

        // Simuler que Uri.parse retourne le mock imageUri
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns imageUri

        // Simuler la réussite du téléchargement de l'image
        coEvery { storageRepository.uploadEventImage(any()) } returns Result.success(pictureUrl)

        // Simuler la réussite de l'enregistrement de l'événement
        coEvery { eventStoreRepository.saveEvent(any()) } returns Result.success(Unit)

        // Appeler la méthode saveEvent
        viewModel.saveEvent("Event Title", "Event Description", "2025-01-01", "12:00", "Event Location", Uri.parse("file://event_image"), authorId)

        // Vérifiez que l'état de l'UI est un succès
        assertTrue(viewModel.uiState.value is UiState.Success)

        // Vérifiez que Uri.parse a été appelé avec le bon argument
        verify { Uri.parse("file://event_image") }
    }


    /**
     * Test the event creation when there is an error during event saving.
     */
    @Test
    fun saveEvent_should_set_error_when_event_save_fails() = runTest {
        val authorId = "author123"
        val imageUri = mockk<Uri>() // Mock the Uri object
        val pictureUrl = "http://example.com/event_image.jpg"

        // Mock the Uri.parse method to return the mocked Uri
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns imageUri

        // Simulate image upload success
        coEvery { storageRepository.uploadEventImage(any()) } returns Result.success(pictureUrl)

        // Simulate event save failure
        coEvery { eventStoreRepository.saveEvent(any()) } returns Result.failure(Exception("Event save failed"))

        // Call the saveEvent method
        viewModel.saveEvent("Event Title", "Event Description", "2025-01-01", "12:00", "Event Location", imageUri, authorId)

        // Verify UI state has error message
        assertTrue(viewModel.uiState.value is UiState.Error)
        val error = (viewModel.uiState.value as UiState.Error).message
        assertEquals("Event save failed", error)
    }

    /**
     * Test the event creation when the image upload fails.
     */
    @Test
    fun saveEvent_should_set_error_when_image_upload_fails() = runTest {
        val authorId = "author123"
        val imageUri = mockk<Uri>() // Mock the Uri object

        // Mock the Uri.parse method to return the mocked Uri
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns imageUri

        // Simulate image upload failure
        coEvery { storageRepository.uploadEventImage(any()) } returns Result.failure(Exception("Image upload failed"))

        // Call the saveEvent method
        viewModel.saveEvent("Event Title", "Event Description", "2025-01-01", "12:00", "Event Location", imageUri, authorId)

        // Verify UI state has error message
        assertTrue(viewModel.uiState.value is UiState.Error)
        val error = (viewModel.uiState.value as UiState.Error).message
        assertEquals("Image upload failed", error)
    }

    /**
     * Test the event creation with valid data.
     */
    @Test
    fun saveEvent_should_create_event_when_data_is_valid() = runTest {
        val authorId = "author123"
        val imageUri = mockk<Uri>() // Mock the Uri object
        val pictureUrl = "http://example.com/event_image.jpg"

        // Mock the Uri.parse method to return the mocked Uri
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns imageUri

        // Simulate image upload success
        coEvery { storageRepository.uploadEventImage(any()) } returns Result.success(pictureUrl)

        // Simulate successful event save
        coEvery { eventStoreRepository.saveEvent(any()) } returns Result.success(Unit)

        // Call the saveEvent method
        viewModel.saveEvent("Event Title", "Event Description", "2025-01-01", "12:00", "Event Location", imageUri, authorId)

        // Verify UI state is success
        assertTrue(viewModel.uiState.value is UiState.Success)
    }

}