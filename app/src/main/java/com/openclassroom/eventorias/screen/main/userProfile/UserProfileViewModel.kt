package com.openclassroom.eventorias.screen.main.userProfile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.openclassroom.eventorias.data.repository.AuthenticationRepository
import com.openclassroom.eventorias.data.repository.UserStoreRepository
import com.openclassroom.eventorias.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing user account.
 */
@HiltViewModel
class UserProfileViewModel @Inject constructor(private val userStoreRepository: UserStoreRepository, private val authRepository: AuthenticationRepository) : ViewModel(){


    //sign out result
    private val _signOutResult = MutableSharedFlow<Result<Unit>>()
    val signOutResult: SharedFlow<Result<Unit>> get() = _signOutResult
    //delete account result
    private val _deleteAccountResult = MutableSharedFlow<Result<Unit>>()
    val deleteAccountResult: SharedFlow<Result<Unit>> get() = _deleteAccountResult

    //user profile data
    private val _currentUserProfile = MutableStateFlow<User?>(null)
    val currentUserProfile: MutableStateFlow<User?> get() = _currentUserProfile

    //notif
    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled: MutableStateFlow<Boolean> get() = _notificationsEnabled



    fun getUserProfile(userId: String) {
        viewModelScope.launch {
            userStoreRepository.getUser(
                userId = userId,
                onSuccess = { user ->
                    _currentUserProfile.value = user
                },
                onFailure = { })
        }
    }

    /**
     * Signs out the current user.
     */
    // Méthode pour déconnecter un utilisateur
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
     * @param user The user to be deleted.
     */
    // Méthode pour supprimer un compte utilisateur
    fun deleteUserAccount(user: FirebaseUser?) {

        viewModelScope.launch {
            if (user != null) {
                //remove user in the  DataBase
                userStoreRepository.deleteUser(
                    userId = user.uid,
                    onSuccess = {
                        //remove user in the Authentication
                        removeUserAuth(user)
                    },
                    onFailure = {})
            }
        }


    }

    /**
     * Removes the user account from the Authentication.
     * @param user The user to be removed.
     */
    fun removeUserAuth(user: FirebaseUser) {
        viewModelScope.launch {
            //remove user in the Authentication
            authRepository.deleteUserAccount(user)
                .collect { result ->
                    _deleteAccountResult.emit(result)
                }
        }
    }






    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }






}