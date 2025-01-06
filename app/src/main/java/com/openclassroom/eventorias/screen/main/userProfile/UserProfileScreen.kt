package com.openclassroom.eventorias.screen.main.userProfile

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseUser
import com.openclassroom.eventorias.ui.theme.eventorias_black
import com.openclassroom.eventorias.ui.theme.eventorias_gray
import com.openclassroom.eventorias.ui.theme.eventorias_loading_gray
import com.openclassroom.eventorias.ui.theme.eventorias_red
import com.openclassroom.eventorias.ui.theme.eventorias_white
import com.openclassroom.eventorias.utils.resizeImage
import kotlinx.coroutines.launch

/**
 * Composable function to display the user profile screen.
 * @param viewModel The view model for the user profile screen.
 * @param onLogOutAction The action to perform when the user logs out.
 * @param currentAuthUser The currently authenticated user.
 * @param innerPadding The padding values for the inner content.
 */
@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel = hiltViewModel(),
    onLogOutAction: () -> Unit,
    currentAuthUser: FirebaseUser? = null,
    innerPadding: PaddingValues
) {
    //Get the current authenticated user
    if (currentAuthUser != null) { viewModel.getUserProfile(currentAuthUser.uid)}

    //The current context
    val currentContext = LocalContext.current
    // Coroutine Scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()
    //Infos Feedbacks
    val snackBarHostState = remember { SnackbarHostState() }
    //TextField config colors
    val textFieldsColors = TextFieldDefaults.colors(
        focusedContainerColor = eventorias_gray,
        unfocusedContainerColor = eventorias_gray,
        disabledContainerColor = eventorias_gray,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )

    // --- NOTIFICATION-----------
    //Notification is enabled state
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()

    // --- USER PROFILE-----------
    //Collect the current user profile
    val currentUser by viewModel.currentUserProfile.collectAsStateWithLifecycle()
    val profilePictureUrl by viewModel.profilePictureUrl.collectAsState()
    val profileUpdateState by viewModel.profileUpdateState.collectAsState()
    // Fetch user profile picture url when the current user is loaded
    LaunchedEffect(currentUser){
        viewModel.fetchUserProfilePicture(currentAuthUser?.uid)
    }
    //Launcher on profile update success state
    LaunchedEffect(profileUpdateState) {
        if(profileUpdateState is UpdatePPState.Success){
            //get user profile picture from storage
            viewModel.fetchUserProfilePicture(currentAuthUser?.uid)
        }
    }

    // --- LOG OUT-----------
    //Dialog state of log out
    var showLogOutDialog by remember { mutableStateOf(false) }
    //Result of sign out from viewmodel
    val signOutResult by viewModel.signOutResult.collectAsStateWithLifecycle(null)
    //Check if the sign out result indicates success or failure
    LaunchedEffect(signOutResult) {
        signOutResult?.onSuccess {
            // Go to authentication screen.
            onLogOutAction()
        }?.onFailure {
            // show message to user for the sign out failure
            snackBarHostState.showSnackbar("Logout failed, please try again.")
        }
    }

    // --- DELETE ACCOUNT-----------
    //Dialog state of account deletion
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    //Result of delete account from viewmodel
    val deleteUserAccountResult by viewModel.deleteAccountResult.collectAsStateWithLifecycle(null)
    //check account deletion result success or failure
    LaunchedEffect(deleteUserAccountResult){
        deleteUserAccountResult?.onSuccess {
            // go to autentification screen.
            onLogOutAction()
        }?.onFailure {
            snackBarHostState.showSnackbar("Account deletion failed, please try again.")
        }
    }

    // Composition ##############################
    Scaffold(
        modifier = Modifier.padding(0.dp),
        topBar = {
            UserProfileTopBar(
                userProfileImageUrl =
                if(profilePictureUrl != null){
                    // Custom user picture from storage
                    profilePictureUrl
                }else{
                    //Google sign in  user picture
                    if(currentAuthUser != null){
                        currentAuthUser.photoUrl?.toString()
                    }else{ null }
                },
                onUserPictureSelected = {uri ->
                    uri?.let {
                        if(currentAuthUser!=null){
                            //Update user profile picture (with resize)
                            val imageUri = uri
                            val resizedImageUri = resizeImage(currentContext, imageUri, maxHeight =  90)
                            if (resizedImageUri != null) {
                                viewModel.uploadUserProfilePicture(currentAuthUser.uid,resizedImageUri)
                            }
                        }
                    }
                }
            )
        },
        content = { padding ->
            // content root
            Box(modifier = Modifier.fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()),
                contentAlignment = Alignment.TopCenter) {

                //--- User Profile content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    //Field for the user's name
                    TextField(
                        value = if(currentUser!=null)"${currentUser?.firstname} ${currentUser?.lastname}" else " loading ...",
                        onValueChange = {},
                        label = { Text("Name") },
                        textStyle = TextStyle(color = Color.White),
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldsColors,
                        shape = RoundedCornerShape(4.dp)
                    )
                    //Field for the email.
                    TextField(
                        value = currentUser?.email ?: "loading ...",
                        onValueChange = { },
                        label = { Text("Email") },
                        readOnly = true,
                        textStyle = TextStyle(color = Color.White),
                        modifier = Modifier.fillMaxWidth(),
                        colors = textFieldsColors,
                        shape = RoundedCornerShape(4.dp)
                    )
                    // Notifications switch selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val permission = Manifest.permission.POST_NOTIFICATIONS
                                    val context = currentContext as Activity
                                    ActivityCompat.requestPermissions(context, arrayOf(permission), 101)
                                }
                                viewModel.setNotificationsEnabled(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                uncheckedThumbColor = Color.Gray,
                                checkedTrackColor = eventorias_red,
                                uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)),
                            modifier = Modifier.width(40.dp).height(24.dp).padding(start = 0.dp, end = 16.dp)
                        )
                        Text(
                            text = "Notifications",
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }

                //--- Sign out and Delete Account buttons
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    // Button to log out
                    Button(
                        onClick = {
                            //show confirme dialog
                            showLogOutDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                            .height(52.dp)
                            .padding(start = 16.dp, end = 16.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = eventorias_red,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Sign out")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Button to delete account
                    Button(
                        onClick = {
                            showDeleteAccountDialog = true
                        },
                        modifier =Modifier.fillMaxWidth().height(52.dp)
                            .padding(start = 16.dp, end = 16.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = eventorias_gray,
                            contentColor = eventorias_loading_gray
                        )
                    ) {
                        Text("Delete account")
                    }
                }

                // Dialog to Log out validation
                if (showLogOutDialog) {
                    LogActionDialog(
                        onDismiss = { showLogOutDialog = false },
                        onValidate = {
                            //logout
                            viewModel.signOutUser()
                            showLogOutDialog = false
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar("Deconnexion en cours...")
                            }
                        },
                        typeOfDialog = 1
                    )
                }

                // Dialog to delete account validation
                if (showDeleteAccountDialog) {
                    LogActionDialog(
                        onDismiss = { showDeleteAccountDialog = false },
                        onValidate = {
                            //logout
                            viewModel.deleteUserAccount(user = currentAuthUser)
                            showDeleteAccountDialog = false
                            coroutineScope.launch {
                                snackBarHostState.showSnackbar("Deleting your account...")
                            }
                        },
                        typeOfDialog = 2
                    )
                }

                // SnackBar host to inform user
                SnackbarHost(hostState = snackBarHostState)
            }
        }
    )
}


/**
 * Composable function to display the user profile top bar.
 * @param onUserPictureSelected The action to perform when a user picture is selected.
 * @param userProfileImageUrl The URL of the user's profile image.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileTopBar(
    onUserPictureSelected: (Uri?) -> Unit,
    userProfileImageUrl: String? = null
){
    TopAppBar(
        title = {
            Text(text ="User profile",
                color = eventorias_white,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(eventorias_black),
        actions = {
            UserProfileImageButton(
                userProfileImageUrl = userProfileImageUrl,
                onImageSelected = onUserPictureSelected
            )
        }
    )
}


/**
 * A composable function to display a log-out or account deletion confirmation dialog.
 * @param onDismiss The action to perform when the dialog is dismissed.
 * @param onValidate The action to perform when the validation button is clicked.
 * @param typeOfDialog The type of dialog to display (1 for log out, 2 for account deletion).
 */
@Composable
fun LogActionDialog(onDismiss: () -> Unit, onValidate: () -> Unit, typeOfDialog: Int) {
    val title = when(typeOfDialog){
        1 -> "Logout"
        2 -> "Delete your account"
        else -> ""
    }
    val message = when(typeOfDialog){
        1 -> "Are you sure to log out ?"
        2 -> "Are you sure to delete your account ?\nWarning! this action can be reverted."
        else -> ""
    }
    val buttonText = when(typeOfDialog){
        1 -> "Logout"
        2 -> "Delete"
        else -> ""
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, style = TextStyle(color = Color.White, fontSize = 20.sp)) },
        text = {
            Text(text = message)
        },
        confirmButton = {
            Button(onClick = onValidate,
            colors = ButtonDefaults.buttonColors(
                containerColor = eventorias_red,
                contentColor = eventorias_white
            )
            ) {
                Text(text = buttonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}


