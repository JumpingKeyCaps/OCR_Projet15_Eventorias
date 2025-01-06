package com.openclassroom.eventorias.screen.main.eventsFeed

import com.openclassroom.eventorias.domain.Event
/**
 * Represents the state of the events feed.
 */
sealed class EventsFeedState {
    data object Loading : EventsFeedState()
    data class Error(val message: String) : EventsFeedState()
    data class Success(val events: List<Event>) : EventsFeedState()
}