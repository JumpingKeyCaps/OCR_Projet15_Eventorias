package com.openclassroom.eventorias.screen.main.eventCreation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassroom.eventorias.data.repository.EventStoreRepository
import com.openclassroom.eventorias.data.repository.StorageRepository
import com.openclassroom.eventorias.domain.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the EventCreationScreen.
 * @param storageRepository The repository for storage-related operations (hilt injected).
 * @param eventStoreRepository The repository for event-related operations (hilt injected).
 */
@HiltViewModel
class EventCreationViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
    private val eventStoreRepository: EventStoreRepository
) : ViewModel() {

    private val _cameraImageUri = MutableStateFlow<Uri?>(null)
    val cameraImageUri: StateFlow<Uri?> = _cameraImageUri

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState

    /**
     * Function to save an event to the database.
     * @param title The title of the event.
     * @param description The description of the event.
     * @param date The date of the event.
     * @param time The time of the event.
     * @param address The address of the event.
     * @param imageUri The URI of the image associated with the event.
     * @param authorId The ID of the author of the event.
     */
    fun saveEvent(
        title: String,
        description: String,
        date: String,
        time: String,
        address: String,
        imageUri: Uri?,
        authorId: String?
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            // Vérifier si l'utilisateur est authentifié
            if (authorId == null) {
                _uiState.value = UiState.Error("User not authenticated") // Gérer l'erreur ici
                return@launch
            }
            // Validation des champs
            val validationError = validateFields(title, description, date, time, address)
            if (validationError != null) {
                _uiState.value = UiState.Error(validationError)
                return@launch
            }
            try {
                val pictureUrl = imageUri?.let {
                    // Upload image and get the URL
                    storageRepository.uploadEventImage(it).getOrThrow()
                }
                // Create the event object
                val event = Event(
                    title = title,
                    description = description,
                    date = date,
                    time = time,
                    location = address,
                    pictureURL = pictureUrl,
                    participants = emptyList(),
                    authorId = authorId
                )
                // Save the event to Firestore
                eventStoreRepository.saveEvent(event).getOrThrow()
                // Notify success
                _uiState.value = UiState.Success
            } catch (e: Exception) {
                // Handle errors
                _uiState.value = UiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }


    /**
     * Function to validate the fields of the event creation form.
     * @param title The title of the event.
     * @param description The description of the event.
     * @param date The date of the event.
     * @param time The time of the event.
     * @param address The address of the event.
     */
    private fun validateFields(
        title: String,
        description: String,
        date: String,
        time: String,
        address: String
    ): String? {
        if (title.isBlank()) {
            return "Title cannot be empty"
        }
        if (description.isBlank()) {
            return "Description cannot be empty"
        }
        if (date.isBlank()) {
            return "Date cannot be empty"
        }
        if (time.isBlank()) {
            return "Time cannot be empty"
        }
        if (address.isBlank()) {
            return "Address cannot be empty"
        }

        return null
    }

    /**
     * A function to update the UiState to Idle mode.
     */
    fun setUiStateToIdle() {
        _uiState.value = UiState.Idle
    }

    /**
     * Function to set the URI of the camera image.
     * @param uri The URI of the camera image.
     */
    fun setCameraImageUri(uri: Uri) {
        _cameraImageUri.value = uri
    }

}






// Classe UiState pour suivre l'état de l'UI
sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data object Success : UiState()
    data class Error(val message: String) : UiState()
}