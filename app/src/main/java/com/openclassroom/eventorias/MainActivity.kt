package com.openclassroom.eventorias

import android.animation.ObjectAnimator
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.openclassroom.eventorias.navigation.EventoriasNavHost
import com.openclassroom.eventorias.navigation.Screens
import com.openclassroom.eventorias.ui.theme.EventoriasTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setupSplashScreenEndAnimation()
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()


            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) //  ID client Web
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

            val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleGoogleSignInResult(task,navController)
                }
            }



            EventoriasTheme {
                EventoriasNavHost(
                    navHostController = navController,
                    // Check if the user is logged in or not to set the start destination
                    startDestination = if (FirebaseAuth.getInstance().currentUser == null){
                        Log.d("authDebug", "[X][X][X] -  USER IS NOT CONNECTED! --> Navigating to Authentication Screen ")
                        Screens.Authentication.route
                    } else{
                        Log.d("authDebug", "[O][O][O] -  USER IS CONNECTED! --> Navigating to EventsFeed Screen ")
                        Screens.Main.route
                    },
                    onGoogleSignIn = {
                        val signInIntent = googleSignInClient.signInIntent
                        launcher.launch(signInIntent)
                    }
                )
            }
        }
    }




    private fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>, navController: NavHostController) {
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        // Connexion réussie
                        Log.d("SignIn", "Sign in with Google successful: ${authTask.result?.user?.email}")

                        // Naviguer vers l'écran EventsFeed après une connexion réussie
                        navController.navigate(Screens.Main.route) {
                            // Cette ligne est optionnelle si tu veux effacer la pile de navigation pour que l'utilisateur ne puisse pas revenir en arrière.
                            popUpTo(Screens.Authentication.route) { inclusive = true }
                        }

                    } else {
                        // Erreur
                        Log.e("SignIn", "Google Sign in failed", authTask.exception)
                    }
                }
        } catch (e: ApiException) {
            Log.e("SignIn", "Google sign in failed", e)
        }
    }





    /**
     * Method to setup the splash screen end transition animation.
     */
    private fun setupSplashScreenEndAnimation(){
        //Callback that is called when the splash screen is animating to the main content.
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            // Create your custom animation to execute a the end of the splashscreen.
            val slideLeft = ObjectAnimator.ofFloat(splashScreenView, View.TRANSLATION_X,0f,-splashScreenView.width.toFloat())
            slideLeft.interpolator = AccelerateInterpolator()
            slideLeft.duration = 180L
            slideLeft.doOnEnd { splashScreenView.remove() }
          //  slideLeft.start()



            val alphaShading = ObjectAnimator.ofFloat(splashScreenView, View.ALPHA,1.0f,0.0f)
            alphaShading.interpolator = AccelerateInterpolator()
            alphaShading.duration = 300L
            alphaShading.doOnEnd { splashScreenView.remove() }
            alphaShading.start()

        }




    }

}








