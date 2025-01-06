package com.openclassroom.eventorias.screen.main.eventDetails

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.screen.main.eventCreation.generateStaticMapUrl
import com.openclassroom.eventorias.ui.theme.eventorias_black
import com.openclassroom.eventorias.ui.theme.eventorias_gray
import com.openclassroom.eventorias.ui.theme.eventorias_red
import com.openclassroom.eventorias.ui.theme.eventorias_white
import com.openclassroom.eventorias.utils.toFormattedDate
import com.openclassroom.eventorias.utils.toFormattedTime
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale


/**
 * Event details screen composable function.
 * @param eventId The ID of the event to display.
 * @param onBackClicked A function to be called when the back button is clicked.
 * @param viewmodel The ViewModel for this screen.
 */
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun EventDetailsScreen(
    eventId: String,
    onBackClicked: () -> Unit,
    viewmodel: EventDetailsViewModel = hiltViewModel()
) {
    // Collect event details from the ViewModel
    val event by viewmodel.event.collectAsState()

    val currentAuthUser = FirebaseAuth.getInstance().currentUser

    // Load the event details when the screen is displayed
    LaunchedEffect(eventId) {
        viewmodel.loadEventDetails(eventId,currentAuthUser?.uid)
    }

    val userParticipation by viewmodel.userParticipation.collectAsState()


    val context = LocalContext.current
    // État pour les coordonnées GPS
    val coordinates = remember { mutableStateOf<Pair<Double, Double>?>(null) }

    // Fonction pour récupérer les coordonnées via Geocoder
    fun getCoordinatesFromAddress(address: String, onResult: (Pair<Double, Double>?) -> Unit) {
        if (address.isBlank()) {
            onResult(null)
            return
        }

        val geocoder = Geocoder(context, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocationName(address, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(response: List<Address>) {
                    if (response.isNotEmpty()) {
                        val geoAddress = response[0]
                        onResult(Pair(geoAddress.latitude, geoAddress.longitude))
                    } else {
                        onResult(null)
                    }
                }

                override fun onError(errorMessage: String?) {
                    Log.e("Geocoder", "Erreur: $errorMessage")
                    onResult(null)
                }
            })
        } else {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val addresses = geocoder.getFromLocationName(address, 1)
                    withContext(Dispatchers.Main) {
                        if (!addresses.isNullOrEmpty()) {
                            val geoAddress = addresses[0]
                            onResult(Pair(geoAddress.latitude, geoAddress.longitude))
                        } else {
                            onResult(null)
                        }
                    }
                } catch (e: IOException) {
                    Log.e("Geocoder", "Erreur: ${e.message}")
                    withContext(Dispatchers.Main) {
                        onResult(null)
                    }
                }
            }
        }
    }

    // State to detect if the image is loaded
    val detailsImageLoaded = remember { mutableStateOf(false) }
    val detailsImageHeight by animateDpAsState(
        targetValue = if (detailsImageLoaded.value) 349.dp else 1.dp, // Animation de la hauteur
        animationSpec = tween(durationMillis = 1000) // Durée de l'animation
    )

    // event charged
    event?.let { event ->

        // Mettre à jour les coordonnées lorsque l'adresse change
        LaunchedEffect(event.location) {
            getCoordinatesFromAddress(event.location) { result ->
                coordinates.value = result
            }
        }


        val participateValueIsLoaded = remember { mutableStateOf(false) }
        val participateButtonOffsetY by animateDpAsState(
        targetValue = if (participateValueIsLoaded.value) 0.dp else 500.dp,
        animationSpec = tween(durationMillis = 2000) // Durée de l'animation
        )
        userParticipation.let {
            participateValueIsLoaded.value = true
        }

        // State to detect if the image is loaded
        val mapImageLoaded = remember { mutableStateOf(false) }
        val mapImageOffsetX by animateDpAsState(
            targetValue = if (mapImageLoaded.value) 0.dp else 500.dp, // L'image commence à 500.dp à droite, et glisse à 0.dp
            animationSpec = tween(durationMillis = 1500) // Durée de l'animation
        )

        // State to detect if the author image is loaded
        val authorImageLoaded = remember { mutableStateOf(false) }
        val authorImageAlpha by animateFloatAsState(
            targetValue = if (authorImageLoaded.value) 1f else 0f,
            animationSpec = tween(durationMillis = 1000)
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = event.title, // Titre de l'événement
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = eventorias_white,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 8.dp, end = 28.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = eventorias_white
                            )
                        }
                    },

                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = eventorias_black

                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp, end = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Image de l'événement
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(event.pictureURL)
                                .crossfade(true)
                                .diskCachePolicy(CachePolicy.ENABLED)
                                .memoryCachePolicy(CachePolicy.ENABLED)
                                .size(380) // Redimensionne l'image (1st loading opti)
                                .build(),
                            onState = { state ->
                                when (state) {
                                    is AsyncImagePainter.State.Loading -> detailsImageLoaded.value = false
                                    is AsyncImagePainter.State.Success -> detailsImageLoaded.value = true
                                    else -> Unit
                                }
                            }
                        ),
                        contentDescription = "Event picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .height(detailsImageHeight)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Row contenant les informations de date/heure et la photo de l'auteur
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Column avec les icônes et textes (date et heure)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                        ) {
                            // Première Row : Date
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Date Icon",
                                    tint = eventorias_white,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = event.date.toFormattedDate(), // Date de l'événement
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Deuxième Row : Heure
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter =  painterResource(id = R.drawable.baseline_event_time_24),
                                    contentDescription = "Time Icon",
                                    tint = eventorias_white,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = event.time.toFormattedTime(), // Heure de l'événement
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // 3eme Row : participants
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.baseline_face_24)),

                                    contentDescription = "Participant Icon",
                                    tint = eventorias_white,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${event.participants.size} participants",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Image circulaire pour la photo de l'auteur
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(event.authorPictureURL)
                                    .crossfade(true)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .size(90) // Redimensionne l'image (1st loading opti)
                                    .build(),
                                onState = { state ->
                                    when (state) {
                                        is AsyncImagePainter.State.Loading -> authorImageLoaded.value = false
                                        is AsyncImagePainter.State.Success -> authorImageLoaded.value = true
                                        else -> Unit
                                    }
                                }
                            ),
                            contentDescription = "Event picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .size(60.dp)
                                .clip(CircleShape)
                                .graphicsLayer { alpha = authorImageAlpha },
                        )
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // Description de l'événement
                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    // Row contenant l'adresse et la carte
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .graphicsLayer {
                                translationX = mapImageOffsetX.toPx()
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Première Box : Adresse
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                                .padding(16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = event.location,
                                style = MaterialTheme.typography.bodyLarge,
                                color = eventorias_white,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        // Deuxième Box : Carte
                        // Carte statique
                        coordinates.value?.let { (lat, lon) ->
                            val mapUrl = generateStaticMapUrl(lat, lon)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter( model = ImageRequest.Builder(LocalContext.current)
                                        .data(mapUrl)
                                        .crossfade(true)
                                        .diskCachePolicy(CachePolicy.ENABLED)
                                        .memoryCachePolicy(CachePolicy.ENABLED)
                                        .size(350) // Redimensionne l'image (1st loading opti)
                                        .build(),
                                        onState = { state ->
                                            when (state) {
                                                is AsyncImagePainter.State.Loading -> mapImageLoaded.value = false
                                                is AsyncImagePainter.State.Success -> mapImageLoaded.value = true
                                                else -> Unit
                                            }
                                        }
                                    ),
                                    contentDescription = "Map Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(92.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .align(Alignment.Center),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(56.dp))

                    Button(
                        onClick = {
                            if(currentAuthUser != null){
                                if (userParticipation) {
                                    // remove the user to event participation
                                    viewmodel.leaveEvent(userId = currentAuthUser.uid, eventId = eventId)
                                } else {
                                    // set the user participating to event
                                    viewmodel.participateInEvent(userId = currentAuthUser.uid, eventId = eventId)
                                }
                            }
                        },
                        shape = RoundedCornerShape(35.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if(userParticipation) eventorias_gray else eventorias_red,
                            contentColor = eventorias_white,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 26.dp, start = 26.dp,bottom = 26.dp)
                            .height(52.dp)
                            .graphicsLayer {
                                translationY = participateButtonOffsetY.toPx()
                            },
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp) // Espacement entre l'icône et le texte
                        ) {
                            Text(
                                text = if (userParticipation) "Leave this event" else "Participate",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun EventDetailsScreenPreview() {
    EventDetailsScreen(
        eventId = "123",
        onBackClicked = {}
    )
}