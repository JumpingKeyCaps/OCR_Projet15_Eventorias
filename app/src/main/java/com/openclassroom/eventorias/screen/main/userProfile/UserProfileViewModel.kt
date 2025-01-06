package com.openclassroom.eventorias.screen.main.userProfile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.openclassroom.eventorias.data.repository.AuthenticationRepository
import com.openclassroom.eventorias.data.repository.StorageRepository
import com.openclassroom.eventorias.data.repository.UserStoreRepository
import com.openclassroom.eventorias.domain.User
import com.openclassroom.eventorias.notification.NotificationPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the user profile screen.
 */
@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userStoreRepository: UserStoreRepository,
    private val authRepository: AuthenticationRepository,
    private val storageRepository: StorageRepository,
    private val preferences: NotificationPreferences
) : ViewModel(){


    //sign out result
    private val _signOutResult = MutableSharedFlow<Result<Unit>>()
    val signOutResult: SharedFlow<Result<Unit>> get() = _signOutResult

    //delete account result
    private val _deleteAccountResult = MutableSharedFlow<Result<Unit>>()
    val deleteAccountResult: SharedFlow<Result<Unit>> get() = _deleteAccountResult

    //user profile data
    private val _currentUserProfile = MutableStateFlow<User?>(null)
    val currentUserProfile: MutableStateFlow<User?> get() = _currentUserProfile

    //notifications state
    private val _notificationsEnabled = MutableStateFlow(preferences.getNotificationsEnabled())
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled


    /**
     * Retrieves the user profile for the given user ID.
     * @param userId The ID of the user to retrieve the profile for.
     */
    fun getUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                val user = userStoreRepository.getUser(userId)
                _currentUserProfile.value = user
            } catch (_: Exception) {}
        }
    }

    /**
     * Signs out the current user.
     */
    fun signOutUser() {
        viewModelScope.launch {
            authRepository.signOutUser()
                .collect { result ->
                    _signOutResult.emit(result)
                }
        }
    }

    /**
     * Deletes the current user's account.
     * @param user The Firebase user to be deleted.
     */
    fun deleteUserAccount(user: FirebaseUser?) {
        viewModelScope.launch {
            if (user != null) {
                try {
                    // Remove user from the database
                    userStoreRepository.deleteUser(user.uid)
                    // Remove user from authentication
                    removeUserAuth(user)
                } catch (_: Exception) { }
            }
        }
    }

    /**
     * Removes the user account from the Authentication.
     * @param user The Firebase user to be removed.
     */
    private fun removeUserAuth(user: FirebaseUser) {
        viewModelScope.launch {
            authRepository.deleteUserAccount(user)
                .collect { result ->
                    _deleteAccountResult.emit(result)
                }
        }
    }

    /**
     * Sets the notifications enabled state.
     * @param enabled The new notifications enabled state.
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        // "touch" or remove the notification token
        viewModelScope.launch {
            if (enabled) {
                FirebaseMessaging.getInstance().token
            } else {
                FirebaseMessaging.getInstance().deleteToken()
            }
        }
        //save the state in the preferences
        viewModelScope.launch {
            preferences.setNotificationsEnabled(enabled)
            _notificationsEnabled.value = enabled
        }
    }


    // User picture stuff ---------------------

    private val _profileUpdateState = MutableStateFlow<UpdatePPState>(UpdatePPState.Idle)
    val profileUpdateState: StateFlow<UpdatePPState> = _profileUpdateState

    private val _profilePictureUrl = MutableStateFlow<String?>(null)
    val profilePictureUrl: StateFlow<String?> = _profilePictureUrl


    /**
     * Uploads a user profile picture to the storage.
     * @param uid The ID of the user.
     * @param imageUri The URI of the image to be uploaded.
     */
    fun uploadUserProfilePicture(uid: String, imageUri: Uri) {
        viewModelScope.launch {
            _profileUpdateState.value = UpdatePPState.Loading

            try {
                // Step 1: Fetch the current profile picture URL
                val oldImageUrl = userStoreRepository.getUserProfilePicture(uid)

                // Step 2: Delete the old image if it exists
                if (!oldImageUrl.isNullOrEmpty()) {
                    val deleteResult = storageRepository.deleteImage(oldImageUrl)
                    if (deleteResult.isFailure) {
                        _profileUpdateState.value = UpdatePPState.Error("Failed to delete old profile picture.")
                        return@launch
                    }
                }
                // Step 3: Upload the new profile picture
                val uploadResult = storageRepository.uploadUserImage(imageUri)
                if (uploadResult.isSuccess) {
                    val newImageUrl = uploadResult.getOrThrow()

                    // Step 4: Update the user profile picture URL
                    updateUserProfilePicture(uid, newImageUrl)
                    _profileUpdateState.value = UpdatePPState.Success
                } else {
                    _profileUpdateState.value = UpdatePPState.Error("Failed to upload new profile picture.")
                }
            } catch (e: Exception) {
                _profileUpdateState.value = UpdatePPState.Error(e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Update the user profile picture URL in the database.
     * @param userId The ID of the user.
     * @param imageUrl The new URL of the user profile picture.
     */
    private suspend fun updateUserProfilePicture(userId: String, imageUrl: String) {
        _profileUpdateState.value = UpdatePPState.Loading
        try {
            userStoreRepository.updateProfilePicture(userId, imageUrl)
            _profileUpdateState.value = UpdatePPState.Success
        } catch (e: Exception) {
            _profileUpdateState.value = UpdatePPState.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Fetch the user profile picture from the database.
     * @param userId The ID of the user.
     */
    fun fetchUserProfilePicture(userId: String?) {
        if (userId != null) {
            viewModelScope.launch {
                try {
                    val url = userStoreRepository.getUserProfilePicture(userId)
                    _profilePictureUrl.value = url
                } catch (e: Exception) {
                    _profilePictureUrl.value = null
                }
            }
        }
    }


}


/**
 * Sealed class representing the state of the user profile picture update.
 */
sealed class UpdatePPState {
    data object Idle : UpdatePPState()
    data object Loading : UpdatePPState()
    data object Success : UpdatePPState()
    data class Error(val message: String) : UpdatePPState()
}