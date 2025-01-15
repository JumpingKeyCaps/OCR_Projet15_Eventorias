package com.openclassroom.eventorias.screen.main.eventsFeed

import android.util.Log
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.openclassroom.eventorias.R
import com.openclassroom.eventorias.domain.Event
import com.openclassroom.eventorias.ui.theme.authentication_red
import com.openclassroom.eventorias.ui.theme.eventorias_black
import com.openclassroom.eventorias.ui.theme.eventorias_gray
import com.openclassroom.eventorias.ui.theme.eventorias_loading_gray
import com.openclassroom.eventorias.ui.theme.eventorias_white
import com.openclassroom.eventorias.utils.toFormattedDate
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsFeedScreen(
    viewModel: EventsFeedViewModel = hiltViewModel(),
    onEventClicked: (Event) -> Unit,
    onCreateEventClicked: () -> Unit,
    innerPadding: PaddingValues) {

    // State pour la recherche
    var searchQuery by remember { mutableStateOf("") }
    val debounceQuery = remember { mutableStateOf("") }

    viewModel.currentUserId = FirebaseAuth.getInstance().currentUser?.uid


    // State pour la gestion de l'onglet sélectionné
    val eventsFeedState by viewModel.eventsState.collectAsState(initial = EventsFeedState.Loading)

    // Collecte des événements filtrés
    val filteredEvents by viewModel.filteredEvents.collectAsState()

    //for the lazy column scrolled position when sorted
    val lazyListState = rememberLazyListState()

    //le type de tri par date
    val dateSortingType by viewModel.dateSortingType.collectAsState()
    LaunchedEffect(dateSortingType) {
        lazyListState.scrollToItem(0)
    }


    // Met à jour la recherche en temps réel
    LaunchedEffect(searchQuery) {
        debounceQuery.value = searchQuery
        delay(250) // délai de 500ms
        viewModel.updateSortFilteredEvents(debounceQuery.value)
    }

    // State pour savoir si la recherche est active
    var isSearchActive by remember { mutableStateOf(false) }

    //SearchBar anim
    val scaleAnim by animateFloatAsState(
        targetValue = if (isSearchActive) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )

    //TopBar title anim
    val titleAnimOffset by animateDpAsState(
        if (isSearchActive) (-100).dp else 8.dp, label = "" // Moves left when search is active
    )

    val sortOption by viewModel.sortOption.collectAsState()
    var isSegmentedButtonVisible by remember { mutableStateOf(true) }

    // Observer le défilement et ajuster la visibilité du bouton
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset }
            .collect { scrollOffset ->
                isSegmentedButtonVisible = scrollOffset <= 0
            }
    }



    Scaffold(
        topBar = {
            // EVENTS FEED SCREEN TOPBAR
            TopAppBar(
                title = {
                    if (!isSearchActive) {
                        Text(
                            text = stringResource(R.string.eventsFeed_topBar_title),
                            color = eventorias_white,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .offset { IntOffset(x = titleAnimOffset.roundToPx(), y = 0) }
                        )
                    }else{
                        EventSearchBar(
                            onCloseBar = {
                              isSearchActive = !isSearchActive
                            },
                            searchQuery = searchQuery,
                            onSearchQueryChanged = { searchQuery = it },
                            onSearchQueryCleared = { searchQuery = "" },
                            modifier = Modifier.scale(scaleAnim)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(eventorias_black),
                actions = {
                    val eventsFeedTopBarSearchContentDescription = stringResource(R.string.eventsFeed_topBar_search_contentDescription)

                    if(!isSearchActive){
                        IconButton(onClick = { isSearchActive = !isSearchActive },
                            modifier = Modifier.semantics { contentDescription = eventsFeedTopBarSearchContentDescription}
                        ) {
                            Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.eventsFeed_topBar_searchIcon_contentDescription), tint = Color.White)
                        }
                    }

                    //sort button
                    Box {
                        val eventsFeedTopBarSortContentDescription = stringResource(R.string.eventsFeed_topBar_sort_contentDescription)
                        IconButton(onClick = {
                            //change the date order sorting
                            viewModel.setDateSortingType(!dateSortingType)

                        },
                            modifier = Modifier.semantics { contentDescription = eventsFeedTopBarSortContentDescription }
                        ) {
                            Icon(
                                ImageVector.vectorResource(id = R.drawable.baseline_swap_vert_24),
                                contentDescription = stringResource(R.string.eventsFeed_topBar_sortIcon_contentDescription),
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            CreateEventFloatingButton(
                modifier = Modifier.padding(bottom = 56.dp),
                onFabClicked = onCreateEventClicked)
        },
        content = { padding ->
            // Show the events list
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding()
                )
                .background(eventorias_black),
                contentAlignment = Alignment.Center) {
                when (eventsFeedState) {
                    is EventsFeedState.Loading -> {
                        val eventsFeedLoadingContentDescription = stringResource(R.string.eventsFeed_loading_contentDescription)
                        CircularProgressIndicator(
                            color = Color.White,
                            trackColor = eventorias_loading_gray,
                            strokeWidth = 4.dp,
                            modifier = Modifier.semantics { contentDescription = eventsFeedLoadingContentDescription}

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
                                contentDescription = stringResource(R.string.eventsFeed_error_icon_contentDescription),
                                tint = eventorias_gray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.eventsFeed_error_title_text),
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.eventsFeed_error_main_text),
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(22.dp))
                            val eventsFeedErrorRetryContentDescription = stringResource(R.string.eventsFeed_error_retry_contentDescription)
                            Button(
                                onClick = { viewModel.observeEvents() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = authentication_red,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .width(159.dp)
                                    .height(40.dp)
                                    .semantics {
                                        contentDescription = eventsFeedErrorRetryContentDescription
                                    }

                            ) {
                                Text(stringResource(R.string.eventsFeed_error_retry_text))
                            }
                        }
                    }

                    is EventsFeedState.Success -> {

                        Column(modifier = Modifier.fillMaxSize()) {
                            //Sort segmented button
                            SortSegmentedButton(
                                sortOption = sortOption ,
                                isSegmentedButtonVisible = isSegmentedButtonVisible,
                                onSegmentClicked = { sortOption ->
                                    viewModel.updateSortOption(sortOption,debounceQuery.value)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )


                            if (filteredEvents.isEmpty()) {
                                Box(modifier = Modifier.fillMaxSize()){
                                    Column(modifier = Modifier.align(Alignment.Center)) {
                                        //Deco doodle empty search result !
                                        Image(
                                            painter = painterResource(id = R.drawable.search_doodle),
                                            contentDescription = stringResource(R.string.eventsFeed_noEventsFoundImage_contentDescription),
                                            modifier = Modifier
                                                .size(200.dp)
                                                .align(Alignment.CenterHorizontally)
                                        )
                                        Text(
                                            text = stringResource(R.string.eventsFeed_no_events_found_text),
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp)
                                                .align(Alignment.CenterHorizontally)
                                        )
                                    }
                                }
                            } else {
                                // Events list
                                LazyColumn(
                                    state = lazyListState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(
                                            top = 0.dp,
                                            bottom = 0.dp,
                                            start = 18.dp,
                                            end = 18.dp
                                        ),
                                    contentPadding = PaddingValues(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {

                                    items(filteredEvents, key = { event -> event.id }) { event -> // Utilisation de l'ID comme clé unique
                                        EventItem(
                                            event = event,
                                            onClick = { onEventClicked(event) },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}






/**
 * Composable function to display an event item.
 * @param event The event to display.
 * @param onClick The action to perform when the event item is clicked.
 */
@Composable
fun EventItem(
    event: Event,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)

    val isAFinishedEvent = dateFormat.parse(event.date)!! < Calendar.getInstance().time


    // State to detect if the image is loaded
    val imageLoaded = remember { mutableStateOf(true) }


    // State for fade-in animation
    val imageAlpha by animateFloatAsState(
        targetValue = if (imageLoaded.value) 1f else 0f,
        animationSpec = tween(durationMillis = 2500), label = ""
    )



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .alpha(if (isAFinishedEvent) 0.4f else 1.0f)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = eventorias_gray ,
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            //Author picture
            Box(modifier = Modifier
                .weight(0.20f)
                .fillMaxSize()){
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(event.authorPictureURL)
                            .size(55)
                            .build(),
                        placeholder = painterResource(id = R.drawable.placeholder_profile),
                        error = painterResource(id = R.drawable.placeholder_profile)
                    ),
                    contentDescription = stringResource(R.string.eventItem_AuthorPicture_contentDescription),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(40.dp)
                        .width(40.dp)
                        .clip(CircleShape)
                        .align(Alignment.Center)
                        .graphicsLayer { alpha = imageAlpha }
                        .clearAndSetSemantics { }

                )
            }


            //Title + date
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 12.dp, end = 10.dp)
                .align(Alignment.CenterVertically)
                .weight(0.4f)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.semantics (mergeDescendants = true){
                        heading()
                        contentDescription = event.title
                    }


                )

                Text(
                    text = event.date.toFormattedDate(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(top = 3.dp)
                        .semantics {
                            contentDescription = event.date.toFormattedDate()
                        } ,
                    maxLines = 1

                )

            }
            //picture
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(event.pictureURL)
                        .size(150)
                        .crossfade(true)
                        .diskCachePolicy(CachePolicy.READ_ONLY)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    onState = { state ->
                        when (state) {
                            is AsyncImagePainter.State.Loading -> imageLoaded.value = false
                            is AsyncImagePainter.State.Success -> imageLoaded.value = true
                            else -> Unit
                        }
                    }
                ),
                contentDescription = stringResource(R.string.eventItem_event_picture_contentDescription),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .align(Alignment.CenterVertically)
                    .padding(start = 0.dp)
                    .weight(0.40f)
                    .graphicsLayer { alpha = imageAlpha }
                    .clearAndSetSemantics { }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onCloseBar: () -> Unit,
    onSearchQueryCleared: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isActived by remember { mutableStateOf(false) }

    DockedSearchBar(
        inputField = {
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = { Text(stringResource(R.string.eventSearchBar_placeholderText), fontSize = 16.sp, color = Color.Gray) },
                maxLines = 1,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 16.sp,  // Taille du texte
                    color = Color.White // Couleur du texte
                ),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = stringResource(R.string.eventSearchBar_leadIcon_contentDescription))
                },

                trailingIcon = {
                    IconButton(onClick = {
                        onSearchQueryCleared()
                        onCloseBar()

                    }) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.eventSearchBar_TrailIcon_contentDescription))
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White, // Couleur du texte en focus
                    unfocusedTextColor = Color.Gray, // Couleur du texte hors focus
                    cursorColor = Color.White, // Couleur du curseur
                    focusedIndicatorColor = Color.Transparent, // Indicateur de focus
                    unfocusedIndicatorColor = Color.Transparent // Indicateur hors focus
                ),
                interactionSource = null,
                // Ajustement du padding interne
                modifier = Modifier
                    .fillMaxSize()
                    .height(35.dp)
                    .padding(top = 0.dp, bottom = 0.dp, start = 0.dp, end = 0.dp),

            )
        },
        expanded = isActived,
        onExpandedChange = { newValue -> isActived = newValue },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(8.dp, 0.dp, 8.dp, 0.dp),
        shape = SearchBarDefaults.dockedShape,
        colors = SearchBarDefaults.colors(),
        tonalElevation = SearchBarDefaults.TonalElevation,
        shadowElevation = SearchBarDefaults.ShadowElevation,
        content = {}
    )
}


/**
 * Floating action button composable function to add a new event.
 * @param onFabClicked The action to perform when the FAB is clicked.
 */
@Composable
fun CreateEventFloatingButton(onFabClicked: () -> Unit,modifier: Modifier ) {
    Box(modifier = modifier.fillMaxSize()) {
        FloatingActionButton(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.BottomEnd),
            onClick = {
                // On navigue vers l'écran d'ajout d'événement
                onFabClicked()
            },
            containerColor = authentication_red,
            contentColor = Color.White
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.createEventFloatingButton_contentDescription))
        }
    }
}








@Preview(showBackground = true)
@Composable
fun EventsItemPreview() {
    EventItem(Event(), onClick = {})
}