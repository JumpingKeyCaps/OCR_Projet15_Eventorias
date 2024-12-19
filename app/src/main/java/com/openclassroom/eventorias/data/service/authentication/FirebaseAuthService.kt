package com.openclassroom.eventorias.data.service.authentication

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

/**
 * Firebase Authentication service.
 */
class FirebaseAuthService {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Method to register a new user with email and password.
     * @param email The user's email.
     * @param password The user's password.
     * @return A response object containing the registered FirebaseUser or an Exception.
     */
    suspend fun registerUser(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Method to sign in a user with his email and password.
     * @param email The user's email.
     * @param password The user's password.
     * @return a response object containing the signed-in FirebaseUser or an Exception.
     */
    suspend fun signInUser(email: String, password: String): Result<FirebaseUser?> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Method to sign out the current user.
     * @return a response of the final state of the sign-out operation.
     */
    fun signOutUser(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Method to delete the current user account.
     * @return a response of the final state of the account deletion operation.
     */
    suspend fun deleteUserAccount(user: FirebaseUser): Result<Unit> {
        return try {
            user.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Method to send a password reset email to the user.
     * @param email The user's email.
     * @return a response of the final state of the password reset operation.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}