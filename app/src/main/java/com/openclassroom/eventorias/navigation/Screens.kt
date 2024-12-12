package com.openclassroom.eventorias.navigation

import androidx.navigation.NamedNavArgument

sealed class Screens(
    val route: String,
    val navArguments: List<NamedNavArgument> = emptyList()
) {
    data object EventsFeed : Screens("eventsFeed")

    data object CreateEvent : Screens("createEvent")

    data object Authentication : Screens("authentication")

    data object UserProfile : Screens("userProfile")

    data object EventDetails : Screens("eventDetails/{postId}") {
        fun createRoute(eventId: String): String = "eventDetails/$eventId"
    }
}