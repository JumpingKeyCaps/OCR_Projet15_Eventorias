package com.openclassroom.eventorias.screen.main

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.domain.Event
import com.openclassroom.eventorias.navigation.Screens
import com.openclassroom.eventorias.screen.main.eventsFeed.EventsFeedScreen
import com.openclassroom.eventorias.screen.main.userProfile.UserProfileScreen
import com.openclassroom.eventorias.ui.theme.eventorias_black
import com.openclassroom.eventorias.ui.theme.eventorias_gray
import com.openclassroom.eventorias.ui.theme.eventorias_white

/**
 * Main screen composable function.
 *  - Manages the inner navigation graph for the EventsFeed and UserProfile screens.
 *  - Manages the bottom navigation bar.
 */
@Composable
fun MainScreen(
    onLogOutAction: () -> Unit,
    onCreateEventClicked: () -> Unit,
    onEventClicked: (Event) -> Unit
) {

    val currentUser = FirebaseAuth.getInstance().currentUser
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute  = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {}, // let empty, TopBar is managed by screens inside the inner-navigation graph
        bottomBar = { BottomNavigationBar(currentRoute = currentRoute, navController = navController)}
    ) { innerPadding ->
        // Inner navigation graph
        InnerNavigationGraph(
            navController = navController,
            startDestination = Screens.EventsFeed.route,
            innerPadding = innerPadding,
            onCreateEventClicked = onCreateEventClicked,
            currentUser = currentUser,
            onLogOutAction = onLogOutAction,
            onEventClicked = onEventClicked
        )
    }
}


/**
 * Inner navigation graph composable function.
 * @param navController The navigation controller for the inner navigation graph.
 * @param startDestination The start destination route for the inner navigation graph.
 * @param innerPadding The inner padding values for the inner navigation graph.
 * @param onCreateEventClicked The action to perform when creating an event(from EventsFeed screen FAB).
 * @param currentUser The current Firebase user.
 * @param onLogOutAction The action to perform when logging out (from UserProfile screen).
 * @param onEventClicked The action to perform when clicking on an event (from EventsFeed screen).
 */
@Composable
fun InnerNavigationGraph(
    navController: NavHostController,
    startDestination: String,
    innerPadding: PaddingValues,
    onCreateEventClicked: () -> Unit,
    currentUser: FirebaseUser? = null,
    onLogOutAction: () -> Unit,
    onEventClicked: (Event) -> Unit){

    NavHost(navController, startDestination = startDestination, Modifier.padding(0.dp)) {
        //-- Events feed Screen
        composable(route = Screens.EventsFeed.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut() }
        ) {
            EventsFeedScreen(
                onCreateEventClicked = onCreateEventClicked,
                onEventClicked = {onEventClicked(it)},
                innerPadding = innerPadding
            )
        }
        //-- User profile screen
        composable(route = Screens.UserProfile.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn() },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) + fadeOut() }
        ) {
            UserProfileScreen(currentAuthUser = currentUser, onLogOutAction = {
                onLogOutAction()
            }, innerPadding = innerPadding)
        }
    }
}



/**
 * Bottom navigation bar composable function.
 * @param currentRoute The current route of the inner navigation graph.
 * @param navController The navigation controller for the inner navigation graph.
 */
@Composable
fun BottomNavigationBar(currentRoute: String?, navController: NavHostController) {

    val navbarAccessibilityDescription = stringResource(id = R.string.navigationbar_contentDescription)

    NavigationBar(
        containerColor = eventorias_black,
        contentColor = eventorias_black,
        tonalElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .background(eventorias_black)
            .semantics {
                contentDescription = navbarAccessibilityDescription
            }
            .testTag("BottomNavigationBar"),
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(0.30f))
            //--- Nav Item -> Events feed
            NavigationBarItem(
                modifier = Modifier.weight(0.20f)
                    .testTag("NavigationBarItem_EventsFeed")
                    .semantics (mergeDescendants = true) { contentDescription = "Events feed section selector" },
                selected = currentRoute == Screens.EventsFeed.route,
                onClick = {
                    navController.navigate(Screens.EventsFeed.route) {
                        // Avoid building up a large stack of destinations
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true  }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when re selecting a previously selected item
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_event_24),
                        contentDescription = stringResource(R.string.navigationBarItem_eventsFeed_contentDescription),
                        tint = Color.White,
                        modifier = Modifier.clearAndSetSemantics { }
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.navigationBarItem_eventsFeed_text),
                        color = Color.White,
                        modifier = Modifier.clearAndSetSemantics { }
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = eventorias_white,
                    selectedTextColor = eventorias_white,
                    unselectedIconColor = eventorias_gray,
                    unselectedTextColor = eventorias_gray,
                    indicatorColor = if (currentRoute == Screens.EventsFeed.route) eventorias_gray else Color.Transparent
                )
            )
            //--- Nav Item -> User Profile
            NavigationBarItem(
                modifier = Modifier.weight(0.20f)
                    .testTag("NavigationBarItem_ProfileUser")
                    .semantics (mergeDescendants = true) { contentDescription = "User profile section selector" },
                selected = currentRoute == Screens.UserProfile.route,
                onClick = {
                    navController.navigate(Screens.UserProfile.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = stringResource(R.string.navigationBarItem_userProfile_contentDescription),
                        tint = Color.White,
                        modifier = Modifier.clearAndSetSemantics { }
                    )
                },
                label = {
                    Text(
                        text = stringResource(R.string.navigationBarItem_userProfile_text),
                        color = eventorias_white,
                        modifier = Modifier.clearAndSetSemantics { }
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = eventorias_white,
                    selectedTextColor = eventorias_white,
                    unselectedIconColor = eventorias_gray,
                    unselectedTextColor = eventorias_gray,
                    indicatorColor = if (currentRoute == Screens.UserProfile.route) eventorias_gray else Color.Transparent
                )
            )
            Spacer(modifier = Modifier.weight(0.30f))
        }
    }
}




