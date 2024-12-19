package com.openclassroom.eventorias.data.repository

import com.google.firebase.auth.FirebaseUser
import com.openclassroom.eventorias.data.service.authentication.FirebaseAuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Repository for authentication-related operations.
 */
class AuthenticationRepository @Inject constructor(private val authService: FirebaseAuthService) {

    /**
     * Register a new user with the provided email and password.
     * @param email The user's email.
     * @param password The user's password.
     * @return A flow emitting the result of the registration operation.
     */
    fun registerUser(email: String, password: String): Flow<Result<FirebaseUser?>> = flow {
        emit(authService.registerUser(email, password))
    }

    /**
     * Sign in a user with the provided email and password.
     * @param email The user's email.
     * @param password The user's password.
     * @return A flow emitting the result of the sign-in operation.
     */
    fun signInUser(email: String, password: String): Flow<Result<FirebaseUser?>> = flow {
        emit(authService.signInUser(email, password))
    }

    /**
     * Sign out the current user.
     * @return A flow emitting the result of the sign-out operation.
     */
    fun signOutUser(): Flow<Result<Unit>> = flow {
        emit(authService.signOutUser())
    }

    /**
     * Delete the current user account.
     * @return A flow emitting the result of the account deletion operation.
     */
    fun deleteUserAccount(user: FirebaseUser): Flow<Result<Unit>> = flow {
        emit(authService.deleteUserAccount(user))
    }

    /**
     * Send a password reset email to the user.
     * @param email The user's email.
     * @return A flow emitting the result of the password reset operation.
     */
    fun sendPasswordResetEmail(email: String): Flow<Result<Unit>> = flow {
        emit(authService.sendPasswordResetEmail(email))
    }

}