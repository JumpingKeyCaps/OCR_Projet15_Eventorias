package com.openclassroom.eventorias.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.openclassroom.eventorias.screen.auth.AuthenticationScreen
import com.openclassroom.eventorias.screen.main.MainScreen

@Composable
fun EventoriasNavHost(
    navHostController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination,
    ) {

        //-- Main Screen
        composable(route = Screens.Main.route) {
           //TODO - App main Screen
            MainScreen()
        }



        //-- Authentication Screen
        composable(route = Screens.Authentication.route) {
            //TODO - Authentication Screen
            AuthenticationScreen(
                onNavigateToEventsFeedScreen = {
                    navHostController.navigate(Screens.Main.route){
                        //clean the nav backstack
                        popUpTo(0)
                        launchSingleTop = true
                        restoreState = false
                    }

                }
            )
        }


    }

}

