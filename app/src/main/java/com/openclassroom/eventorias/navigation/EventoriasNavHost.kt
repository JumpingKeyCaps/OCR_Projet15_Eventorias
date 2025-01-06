package com.openclassroom.eventorias.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.openclassroom.eventorias.screen.auth.AuthenticationScreen
import com.openclassroom.eventorias.screen.main.MainScreen
import com.openclassroom.eventorias.screen.main.eventCreation.EventCreationScreen
import com.openclassroom.eventorias.screen.main.eventDetails.EventDetailsScreen

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
            MainScreen(onLogOutAction = {
                    navHostController.navigate(Screens.Authentication.route){
                        //clean the nav backstack
                        popUpTo(0)
                        launchSingleTop = true
                        restoreState = false
                    }
                },
                onCreateEventClicked = {
                    navHostController.navigate(Screens.CreateEvent.route){
                        launchSingleTop = true
                    }
                },
                onEventClicked = {
                    navHostController.navigate(Screens.EventDetails.createRoute(it.id)){
                        launchSingleTop = true
                    }
                }
            )
        }


        //-- Event Details Screen
        composable(route = Screens.EventDetails.route,
            arguments = Screens.EventDetails.navArguments,

        ) { backStackEntry ->
            //TODO - Event Details Screen
            val eventId = backStackEntry.arguments?.getString("postId")
                ?: throw IllegalStateException("Event ID is required")



            EventDetailsScreen(
                eventId = eventId,
                onBackClicked = {
                    navHostController.popBackStack()
                }
            )
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


        //-- Create event Screen
        composable(route = Screens.CreateEvent.route,
            enterTransition = {
                slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) + fadeOut()
            }
            ) {
            //TODO - Create event Screen
            EventCreationScreen(
                onBackClicked = {
                    navHostController.popBackStack() // use the popBackStack() function to navigate back
                }
            )
        }


    }

}

