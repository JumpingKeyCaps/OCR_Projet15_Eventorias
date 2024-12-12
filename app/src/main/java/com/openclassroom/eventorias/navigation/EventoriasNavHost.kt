package com.openclassroom.eventorias.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun EventoriasNavHost(navHostController: NavHostController, startDestination: String) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {

        //-- Events Feed Screen
        composable(route = Screens.EventsFeed.route) {
           //TODO - Events Feed Screen
        }

        //-- Event Details Screen
        composable(route = Screens.EventDetails.route) {
            //TODO - Event Details Screen
        }

        //-- Create Event Screen
        composable(route = Screens.CreateEvent.route) {
            //TODO - Create Event Screen
        }

        //-- Authentication Screen
        composable(route = Screens.Authentication.route) {
            //TODO - Authentication Screen
        }

        //-- User Profile Screen
        composable(route = Screens.UserProfile.route) {
            //TODO - User Profile Screen
        }


    }

}

