package com.openclassroom.eventorias.screen.auth

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Dialog for password recovery.
 * @param onDismiss The action to perform when the dialog is dismissed.
 * @param onSendEmail The action to perform when the send email button is clicked.
 * @param email The email address to send the recovery email to.
 */
@Composable
fun ForgotPasswordDialog(onDismiss: () -> Unit, onSendEmail: () -> Unit, email:String) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Password Recovery",color = Color.White) },
        text = {
            Text("A password recovery email will be sent to $email.")
        },
        confirmButton = {
            Button(onClick = onSendEmail) {
                Text("Send Email")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}