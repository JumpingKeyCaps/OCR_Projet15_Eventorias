package com.openclassroom.eventorias.screen.main.eventsFeed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.ui.theme.authentication_red
import com.openclassroom.eventorias.ui.theme.eventorias_black
import com.openclassroom.eventorias.ui.theme.eventorias_gray
import com.openclassroom.eventorias.ui.theme.eventorias_loading_gray


@Composable
fun EventsFeedScreen(eventsStateMode: MutableState<EventsFeedState>) {

    // Afficher la liste des événements ici
    Box(modifier = Modifier.fillMaxSize().background(eventorias_black),
        contentAlignment = Alignment.Center) {
        when (eventsStateMode.value) {
            is EventsFeedState.Loading -> {
                CircularProgressIndicator(
                    color = Color.White,
                    trackColor = eventorias_loading_gray,
                    strokeWidth = 4.dp
                )
            }

            is EventsFeedState.Error -> {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_error_24), // Remplace avec ton icône d'erreur si besoin
                        contentDescription = "Error",
                        tint = eventorias_gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Error",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "An error has occurred,\nplease try again later.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(22.dp))
                    Button(
                        onClick = { eventsStateMode.value = EventsFeedState.Loading },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = authentication_red,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.width(159.dp).height(40.dp)
                    ) {
                        Text("Try again")
                    }
                }
            }

            is EventsFeedState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    //la list icite !
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventsFeedScreenPreview() {
    EventsFeedScreen(remember { mutableStateOf<EventsFeedState>(EventsFeedState.Loading) })
}