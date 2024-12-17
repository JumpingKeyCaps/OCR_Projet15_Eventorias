package com.openclassroom.eventorias.navigation

import androidx.navigation.NamedNavArgument

sealed class Screens(
    val route: String,
    val navArguments: List<NamedNavArgument> = emptyList()
) {
    data object Main : Screens("Main")

    data object CreateEvent : Screens("createEvent")

    data object Authentication : Screens("authentication")

    data object EventDetails : Screens("eventDetails/{postId}") {
        fun createRoute(eventId: String): String = "eventDetails/$eventId"
    }
}