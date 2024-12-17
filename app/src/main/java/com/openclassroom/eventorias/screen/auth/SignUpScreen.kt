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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.painterResource
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
 * Sign-up screen composition.
 * @param onClickGoSignIn The action to perform when the user clicks on the "Go to Sign In" button.
 * @param onSignUp The action to perform when the user clicks on the "Sign Up" button.
 * @param signUpButtonState The state of the "Sign Up" button.
 */
@Composable
fun SignUpScreen(
    onClickGoSignIn: () -> Unit,
    onSignUp: (String,String,String,String) -> Unit,
    signUpButtonState: Boolean = true
){

    // Local state variables for email and password
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    //local state variables for first name/last name
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

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

            Text(text = "Sign up",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontSize = 26.sp,
            )

            Text(text = "Create a new account!",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(0.dp,8.dp,0.dp,0.dp)
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
                painter = painterResource(id = R.drawable.authentication_signup_doodle),
                contentDescription = "doodle sign up",
                modifier = Modifier.size(200.dp)
                    .align(Alignment.CenterHorizontally)
            )


            Row (modifier = Modifier.fillMaxWidth()){
                // Champs de saisie pour le prenom
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name", style = MaterialTheme.typography.labelMedium) },
                    placeholder = { Text("John", style = MaterialTheme.typography.bodyMedium) },
                    modifier = Modifier.fillMaxWidth().weight(0.5f),
                    textStyle = TextStyle(color = Color.White),
                    singleLine = true,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Champs de saisie pour le nom
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name", style = MaterialTheme.typography.labelMedium) },
                    placeholder = { Text("Doe", style = MaterialTheme.typography.bodyMedium) },
                    modifier = Modifier.fillMaxWidth().weight(0.5f),
                    textStyle = TextStyle(color = Color.White),
                    singleLine = true,
                    maxLines = 1,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(8.dp)
                )


            }


            Spacer(modifier = Modifier.height(6.dp))
            // Champs de saisie pour l'email et le mot de passe
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", style = MaterialTheme.typography.labelMedium) },
                placeholder = { Text("Enter your email address", style = MaterialTheme.typography.bodyMedium) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White),
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(8.dp)
                )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", style = MaterialTheme.typography.labelMedium) },
                placeholder = { Text("Enter your password", style = MaterialTheme.typography.bodyMedium) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White),
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))

        }



        //BOTTOM BOUTON
        Column(
            modifier = Modifier
                .padding(start = 36.dp, end = 36.dp, top = 10.dp,bottom = 52.dp).align(Alignment.BottomCenter)
        ) {

            // Bouton pour valider
            Button(
                onClick = {
                    if (signUpButtonState) {
                        focusManager.clearFocus()
                        onSignUp(email, password, firstName, lastName)
                    }

                },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = authentication_red, // Fond du bouton
                    contentColor = Color.White // Texte en noir
                )
            ) {
                if (signUpButtonState) {
                    Text(text = "Sign Up")
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp), // Taille de la barre
                        color = Color.White, // Couleur de la barre
                        strokeWidth = 2.dp // Largeur du trait
                    )
                }
            }




            Spacer(modifier = Modifier.height(24.dp))
            // Texte pour Sign Up
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Already have an account?",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.clickable { onClickGoSignIn() }.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = " Sign in !",
                    color = Purple40,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.clickable { onClickGoSignIn()  }.align(Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))


        }



    }

}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(
        onClickGoSignIn = {},
        onSignUp = { _, _, _, _ -> },
        signUpButtonState = true
    )
}