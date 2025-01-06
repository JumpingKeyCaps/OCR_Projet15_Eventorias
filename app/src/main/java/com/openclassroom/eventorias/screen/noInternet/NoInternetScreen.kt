package com.openclassroom.eventorias.screen.noInternet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.ui.theme.eventorias_black
import com.openclassroom.eventorias.ui.theme.eventorias_white

/**
 * Composable function to display a screen when there is no internet connection.
 */
@Composable
fun NoInternetScreen() {

    val accessibilityScreenDescription = stringResource(id = R.string.no_internet_screen_description)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(eventorias_black)
            .padding(16.dp)
            .semantics {
                contentDescription = accessibilityScreenDescription
            }
    ){
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.no_internet_title),
                style = MaterialTheme.typography.titleMedium,
                color = eventorias_white,
                modifier = Modifier.clearAndSetSemantics { }
            )
            Spacer(modifier = Modifier.height(26.dp))
            Image(
                painter = painterResource(id = R.drawable.internet_co_doodle),
                contentDescription = stringResource(id = R.string.no_internet_content_description_doodle),
                modifier = Modifier.size(200.dp)
                    .align(Alignment.CenterHorizontally)
                    .clearAndSetSemantics { }
            )

            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = stringResource(id = R.string.no_internet_main_text),
                style = MaterialTheme.typography.bodySmall,
                color = eventorias_white,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(0.dp)
                    .semantics (mergeDescendants = true){
                        heading()
                    }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.no_internet_secondary_text),
                style = MaterialTheme.typography.bodyMedium,
                color = eventorias_white,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.padding(0.dp)
            )
        }
    }
}

