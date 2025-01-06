package com.openclassroom.eventorias.data.repository

import com.openclassroom.eventorias.data.service.firestore.FirestoreService
import com.openclassroom.eventorias.domain.Event
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 *  The repository to managing/provide events data from/to the FireStore service.
 *  @param firestoreService the service to use for FireStore operations (hilt injected).
 */
class EventStoreRepository @Inject constructor(private val firestoreService: FirestoreService){


    /**
     * Function to save an event in Firestore.
     * @param event the event to save.
     * @return a result of success or failure.
     */
    suspend fun saveEvent(event: Event): Result<Unit> {
        return try {
            // Save event logic
            firestoreService.addEvent(event)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Function to get all the events with a flow.
     * @return a flow of list of events.
     */
    fun getEventsFlow(): Flow<List<Event>> {
        return firestoreService.fetchEventsAsFlow()
    }

    /**
     * Function to get an event by its id.
     * @param eventId the id of the event.
     * @return a result with the event or an error.
     */
    suspend fun getEventById(eventId: String): Result<Event?> {
        return try {
            val event = firestoreService.getEventById(eventId)
            Result.success(event)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }


    /**
     * Function to delete an event.
     * @param eventId the id of the event to delete.
     */
    suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            firestoreService.deleteEvent(eventId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Function to add a new user to the participants list of an event.
     * @param eventId the id of the event.
     * @param userId the id of the user to add.
     * @return a result of success or failure.
     */
    suspend fun addParticipant(eventId: String, userId: String): Result<Unit> {
        return try {
            firestoreService.addParticipant(eventId, userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    /**
     * Function to remove a user from the participants list of an event.
     * @param eventId the id of the event.
     * @param userId the id of the user to remove.
     * @return a result of success or failure.
     */
    suspend fun removeParticipant(eventId: String, userId: String): Result<Unit> {
        return try {
            firestoreService.removeParticipant(eventId, userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



}