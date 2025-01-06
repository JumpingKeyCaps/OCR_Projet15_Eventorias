package com.openclassroom.eventorias.screen.main.eventDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassroom.eventorias.data.repository.EventStoreRepository
import com.openclassroom.eventorias.domain.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the EventDetailsScreen.
 * @param eventStoreRepository The repository for event-related operations (hilt injected).
 */
@HiltViewModel
class EventDetailsViewModel @Inject constructor(private val eventStoreRepository: EventStoreRepository) : ViewModel() {

    private val _event = MutableStateFlow<Event?>(Event())
    val event: StateFlow<Event?> = _event.asStateFlow()

    private val _userParticipation = MutableStateFlow<Boolean>(false)
    val userParticipation: StateFlow<Boolean> = _userParticipation.asStateFlow()

    /**
     * Load the details of an event.
     * @param eventId The ID of the event to load.
     */
    fun loadEventDetails(eventId: String, currentUserId: String?) {
        viewModelScope.launch {
            try {
                // Fetch the event and update the state
                val result = eventStoreRepository.getEventById(eventId)
                val fetchedEvent = result.getOrNull()
                _event.value = fetchedEvent
                // Check if the current user is a participant
                fetchedEvent?.let {
                    if(currentUserId != null){
                        userParticipateToEvent(currentUserId)
                    }
                }
            } catch (e: Exception) {
                _event.value = null  // If error, set event to null
            }
        }
    }

    /**
     * Check if the current user participates in the event.
     * @param userId The ID of the current user.
     */
    private fun userParticipateToEvent(userId: String) {
        val currentEvent = _event.value
        _userParticipation.value = currentEvent?.participants?.contains(userId) == true
    }

    /**
     * Add an user to the participant list of the event
     * @param userId the userID to add
     * @param eventId the event ID
     */
    fun participateInEvent(userId: String, eventId: String) {
        viewModelScope.launch {
            val result = eventStoreRepository.addParticipant(eventId, userId)
            if (result.isSuccess) {
                loadEventDetails(eventId,userId)

            } else {
                // Gérer l'erreur (afficher un message, par exemple)
            }
        }
    }

    /**
     * Remove an user to the participant list of the event
     */
    fun leaveEvent(userId: String, eventId: String){
        viewModelScope.launch {
            val result = eventStoreRepository.removeParticipant(eventId, userId)
            if (result.isSuccess) {
                loadEventDetails(eventId,userId)
            } else {
                // Gérer l'erreur (afficher un message, par exemple)
            }
        }
    }



}

/**
 * Sealed class representing the UI state of the EventDetailsScreen.
 */
sealed class EventDetailsUiState {
    data object Loading : EventDetailsUiState()
    data class Success(val event: Event) : EventDetailsUiState()
    data class Error(val message: String) : EventDetailsUiState()
}