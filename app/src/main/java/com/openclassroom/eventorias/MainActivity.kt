package com.openclassroom.eventorias

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
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
            EventoriasTheme {
                EventoriasNavHost(
                    navHostController = navController,
                    // Check if the user is logged in or not to set the start destination
                    startDestination = if (FirebaseAuth.getInstance().currentUser == null){
                        Screens.Authentication.route
                    } else{
                        Screens.EventsFeed.route
                    }
                )
            }
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








