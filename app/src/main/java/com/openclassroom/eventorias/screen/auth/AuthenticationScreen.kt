package com.openclassroom.eventorias.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text

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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.ui.theme.authentication_red
import com.openclassroom.eventorias.ui.theme.authentication_white
import kotlinx.coroutines.launch


/**
 * Authentication screen composable function.
 * @param viewModel The view model for the authentication screen.
 * @param onNavigateToHomeScreen The action to perform when the user is successfully authenticated.
 */
@Composable
fun AuthenticationScreen(
    viewModel: AuthenticationViewModel = hiltViewModel(),
    onNavigateToEventsFeedScreen: () -> Unit,
    onGoogleSignIn: () -> Unit
){

    // État pour contrôler l'affichage du Bottom Sheet
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true // remove the middle state to open in full directly
    )
    //Coroutine Scope
    val coroutineScope = rememberCoroutineScope()
    // Pager State pour changer de page (nouvelle API)
    val pagerState = rememberPagerState { 2 } // Définir la première page

    // Password recovery
    var showRecoveryPasswordDialog by remember { mutableStateOf(false) }
    var recoveryPasswordEmail by remember { mutableStateOf("") }
    // Infos Feedback
    val snackBarHostState = remember { SnackbarHostState() }
    //Sign-in and Sign-up button states (click locked or not)
    var signInButtonState by remember { mutableStateOf(true) }
    var signUpButtonState by remember { mutableStateOf(true) }


    //Sign-in and Sign-up results from the viewmodel
    val signInResult by viewModel.signInResult.collectAsStateWithLifecycle(null)
    val signUpResult by viewModel.signUpResult.collectAsStateWithLifecycle(null)



    // Check if the sign-in result indicates success or failure
    LaunchedEffect(signInResult) {
        signInResult?.onSuccess { user ->
            if (user != null) {
                onNavigateToEventsFeedScreen() // Success : Navigate to the Events Feed Screen
            }
        }?.onFailure { exception ->
            //Failure : Show a snackbar with the error message and unlock the button to retry
            snackBarHostState.showSnackbar("Sign in failed: ${exception.message}")
            signInButtonState = true
        }
    }
    // Check if the sign-Up result indicates success or failure
    LaunchedEffect(signUpResult) {
        signUpResult?.onSuccess { user ->
            if (user != null) { onNavigateToEventsFeedScreen() }
        }?.onFailure { exception ->
            snackBarHostState.showSnackbar("Sign up failed: ${exception.message}")
            signUpButtonState = true
        }
    }



    // Contenu principal avec le Bottom Sheet
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp, end = 15.dp) // Ajout du padding au start et end
                    .height(LocalConfiguration.current.screenHeightDp.dp * 0.95f) // 99% de la hauteur de l'écran
            ) {
                // Contenu de la sheet
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize().align(Alignment.TopCenter)
                ) { page ->
                    // Calculer la position relative de la page par rapport à la page courante
                    val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

                    // Appliquer une transformation cubique pour un effet fluide
                    val cubicEffect = pageOffset * pageOffset * (3 - 2 * pageOffset)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                translationX = size.width * cubicEffect // Effet de translation
                              //  alpha = 1 - (0.3f * kotlin.math.abs(cubicEffect)) // Réduction progressive de l'opacité
                                scaleX = 1 - (0.1f * kotlin.math.abs(cubicEffect)) // Réduction progressive de la taille
                                scaleY = 1 - (0.1f * kotlin.math.abs(cubicEffect))
                            }
                    ) {
                        // Contenu des pages
                        when (page) {
                            0 -> SignInScreen(
                                onClickGoSignUp = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                                onLostPassword = {
                                    recoveryPasswordEmail = it
                                    if (it.isNotEmpty()){
                                        showRecoveryPasswordDialog = true // show dialog

                                    }else{
                                        coroutineScope.launch {
                                            snackBarHostState.showSnackbar("An email is required for password recovery")
                                        }
                                    }
                                },
                                onSignIn = { email, password ->

                                    if (email.isEmpty()) {
                                        coroutineScope.launch {
                                            snackBarHostState.showSnackbar("Please enter your email")
                                            signInButtonState = true
                                        }
                                    }else if (password.isEmpty()){
                                        coroutineScope.launch {
                                            snackBarHostState.showSnackbar("Please enter your password")
                                            signInButtonState = true
                                        }
                                    }else{
                                        signInButtonState = false // lock sign in button
                                        //All is ok to connect
                                        viewModel.signInUser(email = email, password = password)
                                        coroutineScope.launch {
                                            snackBarHostState.showSnackbar("Sign in...")
                                        }
                                    }
                                },
                                signInButtonState = signInButtonState
                            )
                            1 -> SignUpScreen(
                                onClickGoSignIn = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                                onSignUp = { email, password, firstName, lastName ->
                                    signUpButtonState = false // lock sign up button
                                    // Check if the email and password are not empty
                                    if (email.isEmpty()) {
                                        coroutineScope.launch {
                                            snackBarHostState.showSnackbar("An email is required")
                                            signUpButtonState = true
                                        }
                                    }else if (password.isEmpty()){
                                        coroutineScope.launch {
                                            snackBarHostState.showSnackbar("A password is required")
                                            signUpButtonState = true
                                        }
                                    }else if (firstName.isEmpty()){
                                        coroutineScope.launch {
                                            snackBarHostState.showSnackbar("Please set your First Name")
                                            signUpButtonState = true
                                        }
                                    }else if (lastName.isEmpty()){
                                        coroutineScope.launch {
                                            snackBarHostState.showSnackbar("Please set your Last Name")
                                            signUpButtonState = true
                                        }
                                    }else{
                                        //All is ok to create the new account
                                        viewModel.signUpUser(email = email, password = password, firstName = firstName, lastName = lastName)
                                        coroutineScope.launch {
                                            snackBarHostState.showSnackbar("Sign up ...")
                                        }
                                    }
                                },
                                signUpButtonState = signUpButtonState
                            )
                        }
                    }
                }


                // SnackbarHost to inform user in divers action via a snackbar
                SnackbarHost(hostState = snackBarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 42.dp))

            }
        },
        sheetBackgroundColor = Color.Transparent,
    ) {


        // Authentification Screen main layout
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.align(Alignment.TopCenter).padding(top = 220.dp)) {
                // Eventorias Mini ICO
                Image(
                    painter = painterResource(id = R.drawable.eventorias_logo),
                    contentDescription = "App logo",
                    modifier = Modifier.size(64.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(32.dp))
                // Eventorias Logo Title
                Image(
                    painter = painterResource(id = R.drawable.eventorias_logo_full),
                    contentDescription = "App title",
                    modifier = Modifier.width(222.dp).height(21.dp).align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(72.dp))

                //BUTTON GOOGLE SIGN IN --------------------------
                Button(
                    onClick = {onGoogleSignIn()},
                    colors = ButtonDefaults.buttonColors(authentication_white),
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(5.dp)
                        .width(242.dp)
                        .height(52.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(22.dp) // Espace entre l'icône et le texte
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icongoogle), // Utilise `painterResource` pour les fichiers PNG
                            contentDescription = "Icon google",
                            modifier = Modifier
                                .size(24.dp)
                                .weight(0.15f) // Ajuste la proportion
                        )
                        // Texte du bouton
                        Text(text = "Sign in with Google",
                            color = Color.Black,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(.85f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                //BUTTON EMAIL SIGN IN --------------------------
                Button(
                    onClick = {
                        // Affiche le Bottom Sheet lors du clic
                        coroutineScope.launch {
                            bottomSheetState.show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(authentication_red), // Change la couleur du fond
                    shape = RoundedCornerShape(5.dp), // Boutons arrondis
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(5.dp)
                        .width(242.dp)
                        .height(52.dp)
                ) {
                    Row(modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(22.dp) // Espace entre l'icône et le texte
                    ) {
                        // Icone à gauche
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_mail_24), // Votre ressource vectorielle
                            tint = Color.White, // Couleur de l'icône
                            contentDescription = "Icon",
                            modifier = Modifier.size(24.dp)
                                .weight(.15f)
                        )
                        // Texte du bouton
                        Text(text = "Sign in with email",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(.85f)
                        )

                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Dialog for password recovery
        if (showRecoveryPasswordDialog) {
            ForgotPasswordDialog(
                onDismiss = { showRecoveryPasswordDialog = false },
                onSendEmail = {
                    // Send recovery email
                    viewModel.sendPasswordResetEmail(recoveryPasswordEmail)
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar("Un email de réinitialisation du mot de passe vous a ete envoyé!")
                    }
                    showRecoveryPasswordDialog = false
                },
                email = recoveryPasswordEmail
            )
        }




    }
}
