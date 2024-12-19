package com.openclassroom.eventorias.data.repository

import com.openclassroom.eventorias.data.service.firestore.FirestoreService
import com.openclassroom.eventorias.domain.User
import javax.inject.Inject

/**
 * This class provides a repository for accessing and managing User data.
 */
class UserStoreRepository @Inject constructor(private val firestoreService: FirestoreService) {

    /**
     * Adds a new User to the data base using the injected FirestoreService.
     * @param user The User object to be added.
     * @param onSuccess Callback to be executed on successful addition.
     * @param onFailure Callback to be executed on failure.
     */
    fun addUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestoreService.addUser(user, onSuccess, onFailure)
    }

    /**
     * Retrieves a user from the data base using the injected FirestoreService.
     * @param userId the user id to get.
     * @param onSuccess Callback to be executed on successful retrieval.
     * @param onFailure Callback to be executed on failure.
     */
    fun getUser(userId: String, onSuccess: (User) -> Unit, onFailure: (Exception) -> Unit) {
        firestoreService.getUserById(userId,onSuccess, onFailure)
    }




    /**
     * Deletes a user from the data base by his ID
     * @param userId the user id to delete.
     * @param onSuccess Callback to be executed on successful deletion.
     * @param onFailure Callback to be executed on failure.
     */
    fun deleteUser(userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestoreService.deleteUser(userId, onSuccess, onFailure)
    }


}