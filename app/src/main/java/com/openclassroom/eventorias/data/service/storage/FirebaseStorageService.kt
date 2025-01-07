package com.openclassroom.eventorias.data.service.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * FirebaseStorageService is a class responsible for handling Firebase Storage operations.
 */
class FirebaseStorageService {

    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    /**
     * Deletes an image from Firebase Storage.
     * @param imageUrl The URL of the image to be deleted.
     * @return Unit if the deletion is successful.
     * @throws Exception if the deletion fails.
     */
    suspend fun deleteImage(imageUrl: String) {
        return suspendCancellableCoroutine { continuation ->
            val imageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

            imageReference.delete()
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }


    /**
     * Uploads an image to Firebase Storage and returns the URL of the uploaded image.
     * @param imageUri The URI of the image to be uploaded.
     * @param path The path where the image will be uploaded in Firebase Storage.
     * @return The URL of the uploaded image.
     */
    private suspend fun uploadImage(imageUri: Uri, path: String): String {
        return suspendCancellableCoroutine { continuation ->
            val fileReference = storageReference.child("$path/${System.currentTimeMillis()}.jpg")

            fileReference.putFile(imageUri)
                .addOnSuccessListener {
                    // Récupérer l'URL téléchargeable après le succès
                    fileReference.downloadUrl
                        .addOnSuccessListener { uri ->
                            continuation.resume(uri.toString()) // Retourne l'URL
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }




    /**
     * Uploads the user profile picture to Firebase Storage and returns the URL of the uploaded image.
     * @param imageUri The URI of the image to be uploaded.
     * @return The URL of the uploaded image.
     */
    suspend fun uploadUserImage(imageUri: Uri): String {
        return uploadImage(imageUri, "user-profile-pictures")
    }






    /**
     * Uploads an image related to an event to Firebase Storage and returns the URL of the uploaded image.
     * @param imageUri The URI of the image to be uploaded.
     * @return The URL of the uploaded image.
     */
    suspend fun uploadEventImage(imageUri: Uri): String {
        return uploadImage(imageUri, "event-images")
    }




}