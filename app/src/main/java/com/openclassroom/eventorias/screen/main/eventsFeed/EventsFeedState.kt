package com.openclassroom.eventorias.screen.main.eventsFeed

sealed class EventsFeedState {
    data object Loading : EventsFeedState()
    data class Error(val message: String) : EventsFeedState()
    data class Success(val events: List<String>) : EventsFeedState()
}