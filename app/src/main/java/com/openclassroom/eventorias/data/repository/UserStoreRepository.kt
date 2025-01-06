package com.openclassroom.eventorias.data.repository

import com.openclassroom.eventorias.data.service.firestore.FirestoreService
import com.openclassroom.eventorias.domain.User
import javax.inject.Inject

/**
 * This class provides a repository for accessing and managing User data.
 * @param firestoreService The service used to interact with FireStore (hilt injected)
 */
class UserStoreRepository @Inject constructor(private val firestoreService: FirestoreService) {

    /**
     * Adds a new User to the database using the injected FireStoreService.
     * @param user The User object to be added.
     * @throws Exception in case of failure.
     */
    suspend fun addUser(user: User) {
        firestoreService.addUser(user)
    }

    /**
     * Retrieves a user from the database.
     * @param userId The user ID to get.
     * @return The User object retrieved from the database.
     * @throws Exception in case of failure.
     */
    suspend fun getUser(userId: String): User {
        return firestoreService.getUserById(userId)
    }

    /**
     * Deletes a user from the database by their ID.
     * @param userId The user ID to delete.
     * @throws Exception in case of failure.
     */
    suspend fun deleteUser(userId: String) {
        firestoreService.deleteUser(userId)
    }

    /**
     * Updates the user's profile picture in the database.
     * @param uid The user ID to update.
     * @param profilePictureUrl The new profile picture URL.
     * @throws Exception in case of failure.
     */
    suspend fun updateProfilePicture(uid: String, profilePictureUrl: String) {
        firestoreService.updateUserProfilePicture(uid, profilePictureUrl)
    }

    /**
     * Retrieves the user's profile picture URL from the database.
     * @param userId The user ID.
     * @return The profile picture URL.
     * @throws Exception in case of failure.
     */
    suspend fun getUserProfilePicture(userId: String): String? {
        return firestoreService.getUserProfilePicture(userId)
    }

}