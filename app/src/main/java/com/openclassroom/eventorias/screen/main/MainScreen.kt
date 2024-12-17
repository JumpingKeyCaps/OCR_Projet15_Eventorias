package com.openclassroom.eventorias.screen.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.screen.main.eventsFeed.EventsFeedScreen
import com.openclassroom.eventorias.screen.main.eventsFeed.EventsFeedState
import com.openclassroom.eventorias.screen.main.userProfile.UserProfileScreen
import com.openclassroom.eventorias.ui.theme.authentication_red
import com.openclassroom.eventorias.ui.theme.eventorias_black
import com.openclassroom.eventorias.ui.theme.eventorias_gray

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(navController: NavController) {
    // State pour la gestion de l'onglet sélectionné
    val selectedTab = remember { mutableStateOf(0) }

    val eventsFeedStateMode = remember { mutableStateOf<EventsFeedState>(EventsFeedState.Loading) }

    eventsFeedStateMode.value = EventsFeedState.Error("An error occurred")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if(selectedTab.value == 0) "Event list" else "User profile",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 8.dp))},
                colors = TopAppBarDefaults.topAppBarColors(eventorias_black),
                actions = {

                    if(selectedTab.value == 0){

                        // Barre de recherche en haut
                        IconButton(onClick = { /* TODO: action de recherche */ }) {
                            Icon(Icons.Filled.Search, contentDescription = "Rechercher", tint = Color.White)
                        }
                        IconButton(onClick = { /* TODO: action de recherche */ }) {
                            Icon(ImageVector.vectorResource(id = R.drawable.baseline_swap_vert_24), contentDescription = "Notifications", tint = Color.White)
                        }
                    }else{



                    }


                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = eventorias_black,
                contentColor = eventorias_black,
                tonalElevation = 0.dp,
                modifier = Modifier.fillMaxWidth().background(eventorias_black)
            ) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(0.30f))
                    NavigationBarItem(
                        modifier = Modifier.weight(0.20f),
                        selected = selectedTab.value == 0,
                        onClick = { selectedTab.value = 0 },
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
                            indicatorColor = if (selectedTab.value == 0) eventorias_gray else Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        modifier = Modifier.weight(0.20f),
                        selected = selectedTab.value == 1,
                        onClick = { selectedTab.value = 1 },
                        icon = {
                            Icon(
                                Icons.Outlined.Person,
                                contentDescription = "Profil",
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
                            indicatorColor = if (selectedTab.value == 1) eventorias_gray else Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.weight(0.30f))
                }
            }
        },
        floatingActionButton = {

            if(eventsFeedStateMode.value is EventsFeedState.Success){
                FloatingActionButton(onClick = {
                    // Action pour la création d'événements
                    navController.navigate("create_event")
                },
                    containerColor = authentication_red,
                    contentColor = Color.White) {
                    Icon(Icons.Filled.Add, contentDescription = "Ajouter un événement")
                }
            }



        }
    ) { innerPadding ->
        // Gestion du contenu dynamique selon l'onglet sélectionné
        Box(modifier = Modifier.padding(innerPadding)) {
            AnimatedContent(
                targetState = selectedTab.value,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                    } else {
                        (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                    }
                }, label = ""
            ) { tab ->
                when (tab) {
                    0 -> EventsFeedScreen(eventsStateMode = eventsFeedStateMode)
                    1 -> UserProfileScreen()
                }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun MainScreenPreview(){
    MainScreen(rememberNavController())
}