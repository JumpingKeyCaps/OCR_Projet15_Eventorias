package com.openclassroom.eventorias.data.repository

import android.net.Uri
import com.openclassroom.eventorias.data.service.storage.FirebaseStorageService
import javax.inject.Inject

/**
 * Repository for Firebase Storage operations.
 * @param firebaseStorageService The service for Firebase Storage operations (hilt injected).
 */
class StorageRepository @Inject constructor(private val firebaseStorageService: FirebaseStorageService) {

    /**
     * Deletes an image from Firebase Storage.
     * @param imageUrl The URL of the image to be deleted.
     * @return A result indicating success or failure of the deletion.
     */
    suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            firebaseStorageService.deleteImage(imageUrl)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Uploads a user picture to Firebase Storage.
     * @param imageUri The URI of the image to be uploaded.
     * @return the URL of the uploaded image via a result.
     */
    suspend fun uploadUserImage(imageUri: Uri): Result<String> {
        return try {
            val imageUrl = firebaseStorageService.uploadUserImage(imageUri)
            Result.success(imageUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Uploads an event image to Firebase Storage .
     * @param imageUri The URI of the image to be uploaded.
     * @return A result containing the URL of the uploaded image or an exception if the upload fails.
     */
    suspend fun uploadEventImage(imageUri: Uri): Result<String> {
        return try {
            val imageUrl = firebaseStorageService.uploadEventImage(imageUri)
            Result.success(imageUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}