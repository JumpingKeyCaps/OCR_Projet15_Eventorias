package com.openclassroom.eventorias.screen.auth

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.openclassroom.eventorias.ui.theme.eventorias_red
import com.openclassroom.eventorias.ui.theme.eventorias_white

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
            Button(onClick = onSendEmail,
                colors = ButtonDefaults.buttonColors(
                    containerColor = eventorias_red,
                    contentColor = eventorias_white
                )) {
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