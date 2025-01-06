package com.openclassroom.eventorias

import android.animation.ObjectAnimator
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.openclassroom.eventorias.navigation.EventoriasNavHost
import com.openclassroom.eventorias.navigation.Screens
import com.openclassroom.eventorias.screen.noInternet.NoInternetScreen
import com.openclassroom.eventorias.ui.theme.EventoriasTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main activity of the application.
 *
 *  - Manages the splash screen.
 *  - Initializes the navigation graph.
 *  - Verifies the internet connection.
 *
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val networkStatus = HashMap<String, Boolean>() // Pour suivre l'état de chaque réseau

    /**
     * onCreate method of the activity.
     * @param savedInstanceState The saved instance state of the activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setupSplashScreenEndAnimation()
        enableEdgeToEdge()
        setContent {
            //Internet connection checker
            val isInternetConnected = remember { mutableStateOf(isInternetAvailable(this)) }
            DisposableEffect(Unit) {
                val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkCallback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        networkStatus[network.toString()] = true // Marquer ce réseau comme connecté
                        val hasConnection = networkStatus.containsValue(true) // Vérifie si au moins un réseau est connecté
                        isInternetConnected.value = hasConnection
                    }

                    override fun onLost(network: Network) {
                        networkStatus.remove(network.toString()) // Marquer ce réseau comme déconnecté
                        val hasConnection = networkStatus.containsValue(true) // Vérifie si au moins un réseau est connecté
                        isInternetConnected.value = hasConnection
                    }

                    override fun onUnavailable() {
                        isInternetConnected.value = false // Aucune connexion disponible
                    }
                }
                val networkRequest = NetworkRequest.Builder().build()
                connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
                onDispose {
                    connectivityManager.unregisterNetworkCallback(networkCallback)
                }
            }

            //Main navigation
            val navController = rememberNavController()
            EventoriasTheme {
                if(isInternetConnected.value){ // -> internet available !
                    EventoriasNavHost(
                        navHostController = navController,
                        startDestination = if (FirebaseAuth.getInstance().currentUser == null){// -> not logged in
                            Screens.Authentication.route
                        } else{// -> already logged
                            Screens.Main.route
                        }
                    )
                }else{// -> no internet !
                    NoInternetScreen()
                }
            }
        }
    }



    /**
     * Utility function to check is an internet connection is available.
     * @param context The context of the activity.
     * @return True if an internet connection is available, false otherwise.
     */
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } ?: false
    }

    /**
     * Method to setup the splash screen end transition animation.
     */
    private fun setupSplashScreenEndAnimation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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
}








