package com.openclassroom.eventorias.screen.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.navigation.Screens
import com.openclassroom.eventorias.screen.main.eventsFeed.EventsFeedScreen
import com.openclassroom.eventorias.screen.main.eventsFeed.EventsFeedState
import com.openclassroom.eventorias.screen.main.userProfile.UserProfileScreen
import com.openclassroom.eventorias.ui.theme.authentication_red
import com.openclassroom.eventorias.ui.theme.eventorias_black
import com.openclassroom.eventorias.ui.theme.eventorias_gray

/**
 * Main screen composable function.
 */
@Composable
fun MainScreen() {
    // State pour la gestion de l'onglet sélectionné
    val eventsFeedStateMode = remember { mutableStateOf<EventsFeedState>(EventsFeedState.Loading) }
    eventsFeedStateMode.value = EventsFeedState.Error("An error occurred")

    val currentUser = FirebaseAuth.getInstance().currentUser
    Log.d("AuthUser", "MainScreen: Current user ID: ${currentUser?.uid ?: "User not found!"}")

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute  = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = { TopAppBar(currentRoute = currentRoute) },
        bottomBar = { BottomNavigationBar(currentRoute = currentRoute, navController = navController)},
        floatingActionButton = {
            //Show the FAB only on the events feed screen with a success state
            if(eventsFeedStateMode.value is EventsFeedState.Success){
                CreateEventFloatingButton(navController = navController)
            }
        }
    ) { innerPadding ->
        // Inner navigation graph
        NavHost(navController, startDestination = Screens.EventsFeed.route, Modifier.padding(innerPadding)) {
            //-- events feed Screen
            composable(route = Screens.EventsFeed.route) {
                EventsFeedScreen(eventsStateMode = eventsFeedStateMode)
            }
            //-- user profile screen
            composable(route = Screens.UserProfile.route) {
                UserProfileScreen(currentAuthUser = currentUser )
            }
        }

    }
}


/**
 * Top app bar composable function.
 * @param currentRoute The current route of the inner navigation graph.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(currentRoute: String?){
    val isEventFeed = currentRoute == Screens.EventsFeed.route
    TopAppBar(
        title = { Text(text = if(isEventFeed) "Event list" else "User profile",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 8.dp))},
        colors = TopAppBarDefaults.topAppBarColors(eventorias_black),
        actions = {
            if(isEventFeed){
                // EVENTS FEED SCREEN TOPBAR
                IconButton(onClick = { /* TODO: action de recherche */ }) {
                    Icon(Icons.Filled.Search, contentDescription = "Rechercher", tint = Color.White)
                }
                IconButton(onClick = { /* TODO: action de recherche */ }) {
                    Icon(ImageVector.vectorResource(id = R.drawable.baseline_swap_vert_24), contentDescription = "Notifications", tint = Color.White)
                }
            }else{
                //USER PROFIL SCREEN TOPBAR
                //todo - user picture in userprofile case (to be implemented)


            }
        }
    )

}


/**
 * Bottom navigation bar composable function.
 * @param currentRoute The current route of the inner navigation graph.
 * @param navController The navigation controller for the inner navigation graph.
 */
@Composable
fun BottomNavigationBar(currentRoute: String?, navController: NavHostController) {
    NavigationBar(
        containerColor = eventorias_black,
        contentColor = eventorias_black,
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .background(eventorias_black),
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(0.30f))
            //Nav Item - Events feed
            NavigationBarItem(
                modifier = Modifier.weight(0.20f),
                selected = currentRoute == Screens.EventsFeed.route,
                onClick = {
                    navController.navigate(Screens.EventsFeed.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_event_24),
                        contentDescription = "Événements",
                        tint = Color.White
                    )
                },
                label = {
                    Text(
                        text = "Events",
                        color = Color.White
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = if (currentRoute == Screens.EventsFeed.route) eventorias_gray else Color.Transparent
                )
            )
            //Nav Item - User Profile
            NavigationBarItem(
                modifier = Modifier.weight(0.20f),
                selected = currentRoute == Screens.UserProfile.route,
                onClick = {
                    navController.navigate(Screens.UserProfile.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "User profile",
                        tint = Color.White
                    )
                },
                label = {
                    Text(
                        text = "Profile",
                        color = Color.White
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = if (currentRoute == Screens.UserProfile.route) eventorias_gray else Color.Transparent
                )
            )
            Spacer(modifier = Modifier.weight(0.30f))
        }
    }
}


/**
 * Floating action button composable function to add a new event.
 * @param navController The navigation controller for the inner navigation graph.
 */
@Composable
fun CreateEventFloatingButton(navController: NavHostController) {
    FloatingActionButton(
        onClick = {
        // On navigue vers l'écran d'ajout d'événement
        navController.navigate(Screens.CreateEvent.route)
        },
        containerColor = authentication_red,
        contentColor = Color.White
    ) {
            Icon(Icons.Filled.Add, contentDescription = "Ajouter un événement")
      }
}




