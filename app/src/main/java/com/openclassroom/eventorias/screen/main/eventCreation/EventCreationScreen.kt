package com.openclassroom.eventorias.screen.main.eventCreation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseAuth
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.ui.theme.eventorias_black
import com.openclassroom.eventorias.ui.theme.eventorias_gray
import com.openclassroom.eventorias.ui.theme.eventorias_loading_gray
import com.openclassroom.eventorias.ui.theme.eventorias_red
import com.openclassroom.eventorias.ui.theme.eventorias_white
import com.openclassroom.eventorias.utils.resizeImage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.Calendar
import java.util.Locale

/**
 * Screen for creating an event.
 * @param onBackClicked Callback to be invoked when the top bar back button is clicked.
 * @param viewmodel The view model for this screen.(Hilt injected !)
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class,
    DelicateCoroutinesApi::class
)
@Composable
fun EventCreationScreen(
    onBackClicked: () -> Unit,
    viewmodel: EventCreationViewModel = hiltViewModel()
) {

    val textFieldColors = TextFieldDefaults.colors(
        focusedTextColor = eventorias_white,
        unfocusedTextColor = eventorias_white,
        disabledTextColor = eventorias_white.copy(alpha = 0.38f),
        focusedLabelColor = eventorias_white.copy(alpha = 0.38f),
        unfocusedLabelColor = eventorias_white.copy(alpha = 0.38f),
        focusedContainerColor = eventorias_gray,
        unfocusedContainerColor = eventorias_gray,
        disabledContainerColor = eventorias_gray.copy(alpha = 0.12f),
        cursorColor = Color.White,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
    )

    // Etats des TextField
    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val date = remember { mutableStateOf("") }
    val time = remember { mutableStateOf("") }
    val address = remember { mutableStateOf("") }

    // Context
    val context = LocalContext.current

    // State of pictures
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val selectedPhotoUri = remember { mutableStateOf<Uri?>(null) }

    val cameraImageUri by viewmodel.cameraImageUri.collectAsState()

    // Variable pour suivre la dernière image sélectionnée
    val lastSelectedImage = remember { mutableStateOf<Uri?>(null) }
    // La logique de sélection de l'image, pour choisir la dernière image sélectionnée
    val imageToDisplayUri: Uri? = lastSelectedImage.value

    // Permissions and launchers
    val cameraPermissionState = rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    // Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri.value = uri
        lastSelectedImage.value = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            cameraImageUri?.let { uri ->
                selectedPhotoUri.value = uri
                lastSelectedImage.value = uri
            }
        }
    }
    // Observer l'état des permissions
    LaunchedEffect(cameraPermissionState.status.isGranted) {
        if (cameraPermissionState.status.isGranted && cameraImageUri != null) {
            cameraLauncher.launch(cameraImageUri!!)
        }
    }

    // Prepare a file for storing the camera image
    fun createImageFile(): Uri {
        val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    // Fonction pour demander la permission et lancer la caméra
    fun handleCameraClick() {
        val uri = createImageFile()
        viewmodel.setCameraImageUri(uri)

        if (cameraPermissionState.status.isGranted) {
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionState.launchPermissionRequest()
        }
    }




    // Observer the UI state
    val uiState by viewmodel.uiState.collectAsState()

    // Snackbar state
    val snackbarHostState = remember { SnackbarHostState() }

    // Show Snackbar based on UI state
    LaunchedEffect(uiState) {

        Log.d("SAVEevent","UIstate updated ! ==> $uiState")

        when (val state = uiState) {
            is UiState.Error -> {
                Log.d("SAVEevent","error msg call")
                snackbarHostState.showSnackbar(state.message)
                //reset UI state to idle after showing the error message
                viewmodel.setUiStateToIdle()
            }
            is UiState.Success -> {
                Log.d("SAVEevent","Success msg call")
                snackbarHostState.showSnackbar(
                    message = "Event created successfully!",
                    duration = SnackbarDuration.Short)
                onBackClicked()//go to main screen
            }
            else -> Unit
        }
    }




     //Coordonnées GPS (latitude, longitude)
    val coordinates = remember { mutableStateOf<Pair<Double, Double>?>(null) }
    // Fonction pour obtenir les coordonnées via Geocoder
    fun getCoordinatesFromAddress(address: String, onResult: (Pair<Double, Double>?) -> Unit) {
        if (address.isBlank()) {
            onResult(null) // Adresse invalide
            return
        }

        val geocoder = Geocoder(context, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // API 33 et plus
            geocoder.getFromLocationName(address, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(response: List<Address>) {
                    if (response.isNotEmpty()) {
                        val geoAddress  = response[0]
                        onResult(Pair(geoAddress.latitude, geoAddress.longitude)) // Retourne les coordonnées
                    } else {
                        onResult(null) // Pas de résultats trouvés
                    }
                }

                override fun onError(errorMessage: String?) {
                    Log.e("Geocoder", "Erreur: $errorMessage")
                    onResult(null) // Retourne null en cas d'erreur
                }
            })
        } else {
            // Pour les versions antérieures à API 33
            // Utilisation de Coroutine ou d'un Thread pour éviter de bloquer le thread principal
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val addresses = geocoder.getFromLocationName(address, 1)
                    withContext(Dispatchers.Main) {
                        if (!addresses.isNullOrEmpty()) {
                            val geoAddress = addresses[0]
                            onResult(Pair(geoAddress.latitude, geoAddress.longitude)) // Retourne les coordonnées
                        } else {
                            onResult(null) // Pas de résultats trouvés
                        }
                    }
                } catch (e: IOException) {
                    Log.e("Geocoder", "Erreur: ${e.message}")
                    withContext(Dispatchers.Main) {
                        onResult(null) // Retourne null en cas d'erreur
                    }
                }
            }
        }
    }

// Met à jour les coordonnées quand l'adresse change
    LaunchedEffect(address.value) {
        getCoordinatesFromAddress(address.value) { result ->
            coordinates.value = result
        }
    }





    //COMPO PRINCIPAL #############################################################
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 0.dp)) },
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Creation of an event",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = eventorias_black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.shadow(0.dp)
            )

        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(eventorias_black)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Event Image
                DisplayImage(uri = imageToDisplayUri)

                // Text Fields for the title and the description of the event
                EventTextField(
                    label = "Title",
                    value = title.value,
                    onValueChange = { title.value = it },
                    colors = textFieldColors,
                    placeholderText = "New event")
                EventTextField(
                    label = "Description",
                    value = description.value,
                    onValueChange = { description.value = it },
                    colors = textFieldColors,
                    placeholderText = "Tap here to enter your description")

                // Date and Time picker row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DatePickerField(date, context, textFieldColors, Modifier.weight(1f))
                    TimePickerField(time, context, textFieldColors, Modifier.weight(1f))
                }

                // Address Field
                EventTextField(
                    label = "Address",
                    value = address.value,
                    onValueChange = { address.value = it },
                    colors = textFieldColors,
                    placeholderText = "Enter full address")


                // Afficher la carte statique si les coordonnées sont disponibles
                coordinates.value?.let { (lat, lon) ->
                    val mapUrl = generateStaticMapUrl(lat, lon)
                    Image(
                        painter = rememberAsyncImagePainter(mapUrl),
                        contentDescription = "Static Map",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(start = 8.dp, end = 8.dp)
                            .fillMaxSize()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))

                    )
                }




                // Photo and Picture attachments pickers
                Row(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 30.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // ----------> PHOTO FROM CAMERA
                    Button(
                        onClick = {
                            handleCameraClick()
                        },
                        modifier = Modifier
                            .size(52.dp), // Taille fixe pour le bouton
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                        ),
                        contentPadding = PaddingValues(0.dp) // Retire les marges internes du bouton
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_photo_camera_24),
                            contentDescription = "Add photo",
                            tint = eventorias_gray,
                            modifier = Modifier.size(24.dp) // Taille exacte de l'icône
                        )
                    }

                    // ----------> ATTACHEMENT FROM GALLERY
                    Button(
                        onClick = {
                            imagePickerLauncher.launch("image/*")
                        },
                        modifier = Modifier
                            .size(52.dp), // Taille fixe pour le bouton
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = eventorias_red,
                        ),
                        contentPadding = PaddingValues(0.dp) // Retire les marges internes du bouton
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_attach_file_24),
                            contentDescription = "Add Attachment from gallery",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp) // Taille exacte de l'icône
                        )
                    }
                }

                Spacer(modifier = Modifier.height(250.dp))

            }

            // Submit Button
            Button(
                enabled = uiState !is UiState.Loading && uiState !is UiState.Success,
                onClick = {



                    // Redimensionner l'image avant de l'envoyer au ViewModel
                    // permet de ne pas surcharger le storage
                    val imageUri = selectedImageUri.value
                    val resizedImageUri = imageUri?.let {
                        resizeImage(context, it, maxHeight =  400)
                    }


                    viewmodel.saveEvent(
                        title = title.value,
                        description = description.value,
                        date = date.value,
                        time = time.value,
                        address = address.value,
                        imageUri = resizedImageUri,
                        authorId = FirebaseAuth.getInstance().currentUser?.uid
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = 0.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if(uiState !is UiState.Loading) eventorias_red else eventorias_gray,
                    contentColor = Color.White
                )
            ) {
                if (uiState is UiState.Loading) {
                    // Afficher le Progress Indicator pendant le chargement
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),  // Taille du progress indicator
                        color = Color.White
                    )
                } else {
                    // Afficher le texte quand ce n'est pas en cours de chargement
                    Text(text = "Validate")
                }
            }

        }
    }


}



// Fonction pour générer l'URL de la carte statique
@Composable
fun generateStaticMapUrl(lat: Double, lon: Double): String {
    val apiKey = stringResource(id = R.string.google_maps_api_key)
    return "https://maps.googleapis.com/maps/api/staticmap?center=$lat,$lon&zoom=15&size=600x300&markers=$lat,$lon&key=$apiKey"
}



/**
 * A composable function to display an image from the given URI.
 * @param uri The URI of the image to be displayed.
 */
@Composable
fun DisplayImage(uri: Uri?) {
    LaunchedEffect(uri) {
        Log.d("uricamera", "URI updated: $uri")
    }
    uri?.let {
        Image(
            painter = rememberAsyncImagePainter(uri),
            contentDescription = "Event picture",
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )
    }
}


/**
 * A composable function to display a Specific text field.
 * @param label The label of the text field.
 * @param value The current value of the text field.
 * @param onValueChange The callback to be invoked when the value of the text field changes.
 * @param colors The colors to be applied to the text field.
 * @param placeholderText The text to be displayed when the text field is empty.
 */
@Composable
fun EventTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    colors: TextFieldColors,
    placeholderText: String) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholderText) },
        modifier = Modifier.fillMaxWidth(),
        colors = colors,
        shape = RoundedCornerShape(4.dp)
    )
}


/**
 * A composable function to display a date picker field.
 * @param state The state of the date picker field.
 * @param context The context of the application.
 * @param colors The colors to be applied to the text field.
 * @param modifier The modifier to be applied to the text field.
 */
@Composable
fun DatePickerField(state: MutableState<String>, context: Context, colors: TextFieldColors,modifier: Modifier) {
    TextField(
        value = state.value,
        onValueChange = { state.value = it },
        label = { Text("Date") },
        placeholder = { Text("MM/DD/YYYY") },
        readOnly = true,
        modifier = modifier,
        colors = colors,
        shape = RoundedCornerShape(4.dp),
        trailingIcon = {
            IconButton(onClick = {
                val calendar = Calendar.getInstance()
                // Obtenir la date actuelle
                val today = Calendar.getInstance()

                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        // Mettre à jour l'état de la date sélectionnée
                        state.value = "${month+1}/$dayOfMonth/$year"
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    // Configurer la date minimale pour interdire les dates passées
                    datePicker.minDate = today.timeInMillis
                }.show()
            }) {
                Icon(imageVector = Icons.Default.DateRange,
                    contentDescription = "Select Date",
                    tint = eventorias_loading_gray)
            }
        }
    )
}


/**
 * A composable function to display a time picker field.
 * @param state The state of the time picker field.
 * @param context The context of the application.
 * @param colors The colors to be applied to the text field.
 */
@Composable
fun TimePickerField(state: MutableState<String>, context: Context, colors: TextFieldColors,modifier: Modifier) {
    TextField(
        value = state.value,
        onValueChange = {state.value = it },
        label = { Text("Time") },
        placeholder = { Text("HH : MM") },
        readOnly = true,
        modifier = modifier,
        colors = colors,
        shape = RoundedCornerShape(4.dp),
        trailingIcon = {
            IconButton(onClick = {
                val calendar = Calendar.getInstance()
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        state.value = "$hour:$minute"
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_event_time_24),
                    contentDescription = "Select Time",
                    tint = eventorias_loading_gray
                )
            }
        }
    )
}






@Preview(showBackground = true)
@Composable
fun EventCreationScreen_preview() {
    EventCreationScreen(onBackClicked = {})
}



