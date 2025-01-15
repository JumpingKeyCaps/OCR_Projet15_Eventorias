package com.openclassroom.eventorias.domain

import androidx.annotation.Keep
import java.io.Serializable

/**
 * Event data model.
 * Represents an event with his various attributes.
 * @property id The unique identifier of the event.
 * @property title The title of the event.
 * @property description The description of the event.
 * @property date The date of the event.
 * @property time The time of the event.
 * @property location The location of the event.
 * @property pictureURL The URL of the event's picture.
 * @property participants The list of participants of the event.
 * @property authorId The ID of the event's author.
 * @property authorPictureURL The URL of the event's author's picture.
 */
@Keep
data class Event (
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "",
    val pictureURL: String? = null,
    val participants: List<String> = emptyList(),
    val authorId: String = "",
    val authorPictureURL: String? = null


) : Serializable