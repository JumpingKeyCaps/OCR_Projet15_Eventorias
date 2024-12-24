package com.openclassroom.eventorias.screen.main.userProfile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseUser
import com.openclassroom.eventorias.ui.theme.eventorias_gray
import com.openclassroom.eventorias.ui.theme.eventorias_loading_gray
import com.openclassroom.eventorias.ui.theme.eventorias_red
import kotlinx.coroutines.launch


@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel = hiltViewModel(),
    onLogOutAction: () -> Unit,
    currentAuthUser: FirebaseUser? = null
) {

    if (currentAuthUser != null) {
        viewModel.getUserProfile(currentAuthUser.uid)
    }

    val currentUser by viewModel.currentUserProfile.collectAsStateWithLifecycle()





    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()

    // Coroutine Scope for launching coroutines
    val coroutineScope = rememberCoroutineScope()
    //Infos Feedback
    val snackBarHostState = remember { SnackbarHostState() }




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





    val textFieldsColors = TextFieldDefaults.colors(
        focusedContainerColor = eventorias_gray,
        unfocusedContainerColor = eventorias_gray,
        disabledContainerColor = eventorias_gray,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )



    // Composition root
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {

        //--- User Profile content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Champ de texte pour le nom et prénom
            TextField(
                value = if(currentUser!=null)"${currentUser?.firstname} ${currentUser?.lastname}" else " loading ...",
                onValueChange = {
                  //  viewModel.updateFirstName(it)
                },
                label = { Text("Name") },
                textStyle = TextStyle(color = Color.White),
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldsColors,
                shape = RoundedCornerShape(4.dp)
            )

            // Champ de texte pour l'email
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

            // Switch pour activer/désactiver les notifications
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = {
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

            // Button pour se déconnecter
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

            // Button pour supprimer le compte
            Button(
                onClick = {
                    showDeleteAccountDialog = true
                },
                modifier =Modifier.fillMaxWidth()
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


        // Dialog of Log out validation
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



        // Dialog of delete account validation
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


        // Snackbar host to inform user
        SnackbarHost(hostState = snackBarHostState)

    }

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
            Button(onClick = onValidate) {
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











@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {
    UserProfileScreen(onLogOutAction = {})
}