package com.openclassroom.eventorias.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.ui.theme.Purple40
import com.openclassroom.eventorias.ui.theme.authentication_red
import com.openclassroom.eventorias.ui.theme.eventorias_black

/**
 * Sign-in screen composable function.
 *
 * @param onClickGoSignUp The action to perform when the user clicks on the "Go to Sign Up" button.
 * @param onLostPassword The action to perform when the user clicks on the "Lost Password" button.
 * @param onSignIn The action to perform when the user clicks on the "Sign In" button.
 * @param signInButtonState The state of the "Sign In" button.
 */

@Composable
fun SignInScreen(
    onClickGoSignUp: () -> Unit,
    onLostPassword: (String) -> Unit,
    onSignIn: (String,String) -> Unit,
    signInButtonState: Boolean = true){




    // États pour stocker les valeurs des champs
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Détection d'erreurs (exemple simple)
    val isEmailError = false
    val isPasswordError = false

    // Gestionnaire de focus
    val focusManager = LocalFocusManager.current

    // Utilisation d'un Box pour styliser le contenu avec des coins arrondis
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)) // Coins arrondis en haut
            .background(eventorias_black) // Couleur de fond personnalisée
            .padding(16.dp)
    ) {


        //HEADER
        Column(
            modifier = Modifier
                .padding(start = 36.dp, end = 36.dp, top = 10.dp).align(Alignment.TopCenter)
        ) {

            Text(text = "Sign in",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontSize = 26.sp,
                modifier = Modifier
                    .testTag("authentication_SignInScreen_header_1")
                    .semantics { contentDescription = "Header text: Sign in to your account" }
            )

            Text(text = "with your e-mail address ",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(0.dp,0.dp,0.dp,0.dp)
                    .testTag("authentication_SignInScreen_header_2")
                    .semantics {
                        contentDescription = "Subtitle text: with your email address"
                    }
            )



            Spacer(modifier = Modifier.height(16.dp))
            Divider(
                color = Color.DarkGray, // Couleur de la ligne
                thickness = 1.dp,   // Épaisseur de la ligne
                modifier = Modifier.fillMaxWidth().padding(start = 0.dp,end = 30.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))




            //Deco doodle
            Image(
                painter = painterResource(id = R.drawable.authentication_rafiki_doodle),
                contentDescription = "Doodle sign in image",
                modifier = Modifier.size(200.dp)
                    .align(Alignment.CenterHorizontally)
            )



            // Champs de saisie pour l'email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", style = MaterialTheme.typography.labelMedium)},
                placeholder = { Text("Enter your email", style = MaterialTheme.typography.bodyMedium) },
                isError = isEmailError,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email, // Icône pour représenter l'email
                        contentDescription = "Email Icon",
                        modifier = Modifier.size(20.dp)
                    )
                },

                singleLine = true,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
                    .semantics { contentDescription = "Email input field" }
                    .testTag("authentication_SignInScreen_EmailInput"),
                textStyle = TextStyle(color = Color.White),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            //Champ de saisie pour le mot de passe
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", style = MaterialTheme.typography.labelMedium) },
                placeholder = { Text("Enter your password", style = MaterialTheme.typography.bodyMedium) },
                isError = isPasswordError,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock, // Icône pour représenter l'email
                        contentDescription = "Password Icon",
                        modifier = Modifier.size(20.dp)
                    )
                },
                singleLine = true,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth().semantics {
                    contentDescription = "Password input field"
                }.testTag("authentication_SignInScreen_PasswordInput"),
                textStyle = TextStyle(color = Color.White),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                shape = RoundedCornerShape(8.dp)

            )
            Spacer(modifier = Modifier.height(16.dp))
            // Texte pour mot de passe oublié
            Text(
                text = "Forgot your password?",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.clickable { onLostPassword(email) }.align(Alignment.End)
                    .semantics { contentDescription = "Forgot password link" }
                    .testTag("authentication_SignInScreen_ForgotPassword")
            )


        }


        //BOTTOM BOUTON
        Column(
            modifier = Modifier
                .padding(start = 36.dp, end = 36.dp, top = 10.dp,bottom = 52.dp).align(Alignment.BottomCenter)
        ) {
            // Bouton pour valider
            Button(
                onClick = {
                    if (signInButtonState) {
                        focusManager.clearFocus()
                        onSignIn(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    .testTag("authentication_SignInScreen_SignInButton"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(signInButtonState)authentication_red else Color.DarkGray, // Fond du bouton
                    contentColor = Color.White // Texte en noir
                )
            ) {
                if (signInButtonState) {
                    Text(text = "Sign In")
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                            .testTag("authentication_SignInScreen_circularProgressIndicator"),
                        color = Color.White, // Couleur de la barre
                        strokeWidth = 2.dp // Largeur du trait
                    )
                }
            }
            Spacer(modifier = Modifier.height(22.dp))
            // Texte pour Sign up
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Don't have an account?",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .clickable { onClickGoSignUp() }
                        .align(Alignment.CenterVertically)
                        .testTag("authentication_SignInScreen_GoSignUp_A")
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "Let's Create one !",
                    color = Purple40,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .clickable { onClickGoSignUp() }
                        .align(Alignment.CenterVertically)
                        .testTag("authentication_SignInScreen_GoSignUp_B")
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen(
        onClickGoSignUp = {},
        onLostPassword = {},
        onSignIn = { _, _ -> },
        signInButtonState = true
    )
}
