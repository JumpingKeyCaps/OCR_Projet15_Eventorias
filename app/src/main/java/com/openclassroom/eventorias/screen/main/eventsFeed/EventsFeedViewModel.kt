package com.openclassroom.eventorias.screen.main.eventsFeed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassroom.eventorias.data.repository.EventStoreRepository
import com.openclassroom.eventorias.domain.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * The ViewModel for the EventsFeedScreen.
 * @param eventStoreRepository The repository for event-related operations (hilt injected).
 * @return A ViewModel instance.
 */
@HiltViewModel
class EventsFeedViewModel @Inject constructor(private val eventStoreRepository: EventStoreRepository) : ViewModel() {

    private val _eventsState = MutableStateFlow<EventsFeedState>(EventsFeedState.Loading)
    val eventsState: StateFlow<EventsFeedState> = _eventsState.asStateFlow()

    private val _allEvents = mutableListOf<Event>()

    private val _filteredEvents = MutableStateFlow<List<Event>>(emptyList())
    val filteredEvents: StateFlow<List<Event>> = _filteredEvents.asStateFlow()


    private val _sortOption = MutableStateFlow(SortOption.Soon)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()


    private val _dateSortingType = MutableStateFlow(true) // true for ascending, false for descending
    val dateSortingType: StateFlow<Boolean> = _dateSortingType.asStateFlow()

    private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)

    var currentUserId: String? = null

    /**
     * Initialize the ViewModel and start observing events.
     */
    init {
        observeEvents()
    }

    /**
     * Observe all events and updates the state accordingly.
     */
    fun observeEvents() {
        viewModelScope.launch {
            eventStoreRepository.getEventsFlow()
                .onStart {
                    _eventsState.value = EventsFeedState.Loading
                }
                .catch {
                    _eventsState.value = EventsFeedState.Error("Unable to load events. Please try again.")
                }
                .collect { updatedEvents ->
                    _allEvents.clear()
                    _allEvents.addAll(updatedEvents)

                    updateSortFilteredEvents()

                    _eventsState.value = if (updatedEvents.isEmpty()) {
                        EventsFeedState.Error("No events found.")
                    } else {
                        EventsFeedState.Success(updatedEvents)
                    }
                }
        }
    }

    /**
     * Updates the filtered events based on the current sort option.
     *  - Incoming : keep only the events in the future
     *  - Participate : keep only the events where the user is participating
     *  - Finished : keep only the events in the past
     *  @param query The search query to be applied to the filtered events.
     */
    fun updateSortFilteredEvents(query: String? = null) {

        // 1 - Sorting the events based on the current sort option + apply date sorting mode
        var displayedEvents = when (_sortOption.value) {
            //keep only the events in the future
            SortOption.Soon -> {
                eventsDateSorting(onlyIncomingEvents(_allEvents), _dateSortingType.value)
            }
            //Keep only the events where the user is participating
            SortOption.Participate -> {
                eventsDateSorting(onlyParticipateEvents(_allEvents), _dateSortingType.value)
            }
            //Keep only the events in the past
            SortOption.Finished -> {
                eventsDateSorting(onlyFinishedEvents(_allEvents), _dateSortingType.value)
            }

        }
        //2 - Apply query filter if exist
        if(query != null && query != ""){
            displayedEvents = queryFilteringEvents(events = displayedEvents, query = query)
        }
        //3 - Update the filtered events value
        _filteredEvents.value = displayedEvents


    }

    /**
     * Query filtering events by title.
     * @param events The list of events to be filtered.
     * @param query The search query.
     * @return The filtered list of events.
     */
    private fun queryFilteringEvents(events: List<Event>, query: String): List<Event> {
        return if(query.isEmpty()) events else events.filter { it.title.contains(query, ignoreCase = true)}
    }

    /**
     * Function to sort events by date.
     * @param events The list of events to be sorted.
     * @param isAscendingSort Whether to sort in ascending order (true) or descending order (false).
     * @return The sorted list of events.
     */
    private fun eventsDateSorting(events: List<Event>, isAscendingSort: Boolean = false): List<Event> {
        return if(isAscendingSort){
            events.sortedBy { dateFormat.parse(it.date) }
        }else{
            events.sortedByDescending { dateFormat.parse(it.date) }
        }
    }


    /**
     * Function to filter only incoming events.
     * @param events The list of events to be filtered.
     * @return The filtered list of events.
     */
    private fun onlyIncomingEvents(events: List<Event>): List<Event> {
        return events.filter { event ->
            val eventDate = try { dateFormat.parse(event.date) } catch (e: ParseException) { null }
            eventDate?.after(Date()) == true // Vérifie si la date est dans le futur
        }
    }

    /**
     * Function to filter only finished events.
     * @param events The list of events to be filtered.
     * @return The filtered list of events.
     */
    private fun onlyFinishedEvents(events: List<Event>): List<Event> {
       return events.filter { event ->
           val eventDate = try { dateFormat.parse(event.date) } catch (e: ParseException) { null }
           eventDate?.before(Date()) == true // Vérifie si la date est dans le passé
       }
   }

    /**
     * Function to filter only events where user participate.
     * @param events The list of events to be filtered.
     * @return The filtered list of events.
     */
   private fun onlyParticipateEvents(events: List<Event>): List<Event> {
        return events.filter { event ->
            event.participants.contains(currentUserId) // Vérifie si l'utilisateur participe à l'événement
        }
    }


    /**
     * Updates the sort option and updates the filtered events accordingly.
     * @param newSortOption The new sort option to be set.
     *  - Incoming / Participate / Finished
     *  @param currentQuery The current search query.
     */
    fun updateSortOption(newSortOption: SortOption, currentQuery: String?) {
        //change the sort option
        _sortOption.value = newSortOption
        // call the events update function with the last query value to apply on the list
        updateSortFilteredEvents(currentQuery)
    }


    /**
     * Updates the date sorting type and updates the filtered events accordingly.
     * @param isAscendingSort the new date sorting type to be set.
     */
    fun setDateSortingType(isAscendingSort: Boolean){
        //change date sorting type
        _dateSortingType.value = isAscendingSort
        // call the events update function
        updateSortFilteredEvents()
    }





}

