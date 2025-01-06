package com.openclassroom.eventorias.data.service.firestore

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.openclassroom.eventorias.domain.Event
import com.openclassroom.eventorias.domain.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


/**
 * Service pour interagir avec Firestore.
 */
class FirestoreService {

    private val db = FirebaseFirestore.getInstance("eventoriasdb")




    //----------- User stuff with Suspend ---------------------

    /**
     * Ajoute un utilisateur à Firestore.
     * @param user L'utilisateur à ajouter.
     */
    suspend fun addUser(user: User) {
        db.collection("users")
            .document(user.id) // set user id
            .set(user) // create user or update if already exist
            .await()
    }

    /**
     * Récupère un utilisateur par son ID.
     * @param uid L'ID de l'utilisateur.
     * @return L'utilisateur correspondant à l'ID.
     * @throws Exception si l'utilisateur n'existe pas ou en cas d'erreur.
     */
    suspend fun getUserById(uid: String): User {
        val documentSnapshot = db.collection("users")
            .document(uid)
            .get()
            .await()

        if (!documentSnapshot.exists()) {
            throw Exception("User not found")
        }

        return documentSnapshot.toObject<User>()
            ?: throw Exception("Failed to convert document to User")
    }

    /**
     * Supprime un utilisateur par son ID.
     * @param uid L'ID de l'utilisateur.
     */
    suspend fun deleteUser(uid: String) {
        db.collection("users")
            .document(uid)
            .delete()
            .await()
    }

    /**
     * Met à jour l'URL de la photo de profil de l'utilisateur.
     * @param uid L'ID de l'utilisateur à mettre à jour.
     * @param profilePictureUrl La nouvelle URL de la photo de profil.
     */
    suspend fun updateUserProfilePicture(uid: String, profilePictureUrl: String) {
        db.collection("users")
            .document(uid)
            .update("pictureURL", profilePictureUrl)
            .await()
    }

    /**
     * Récupère l'URL de la photo de profil de l'utilisateur.
     * @param userId L'ID de l'utilisateur.
     * @return L'URL de la photo de profil.
     * @throws Exception si l'utilisateur ou l'URL n'existe pas.
     */
    suspend fun getUserProfilePicture(userId: String): String? {
        val documentSnapshot = db.collection("users")
            .document(userId)
            .get()
            .await()
        return if (!documentSnapshot.exists()) {
            null
        }else{
            documentSnapshot.getString("pictureURL")
        }
    }




    // ---------- Event stuff -------------------------


    /**
     * Ajoute ou met à jour un événement dans Firestore.
     * @param event L'objet Event à ajouter ou mettre à jour.
     */
    suspend fun addEvent(event: Event) {
        val eventId = event.id.ifBlank { db.collection("events").document().id }
        db.collection("events")
            .document(eventId)
            .set(event.copy(id = eventId))
            .await()
    }


    /**
     * Récupère tous les événements depuis Firestore.
     * @return Une liste d'objets Event.
     */
    fun fetchEventsAsFlow(): Flow<List<Event>> = callbackFlow {
        val listenerRegistration = db.collection("events")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Ferme le flow en cas d'erreur
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val events = snapshot.toObjects(Event::class.java)
                    val coroutineScope = CoroutineScope(Dispatchers.IO)

                    val eventsWithPicture = events.map { event ->
                        async {
                            val authorPictureUrl = getUserProfilePictureUrl(event.authorId)
                            event.copy(authorPictureURL = authorPictureUrl)
                        }
                    }

                    coroutineScope.launch {
                        val fetchedEvents = mutableListOf<Event>()
                        for (deferredEvent in eventsWithPicture) {
                            fetchedEvents.add(deferredEvent.await())
                        }
                        trySend(fetchedEvents) // Emit final list
                    }
                } else {
                    trySend(emptyList()) // Émet une liste vide si aucun événement n'est trouvé
                }
            }

        // Assurez-vous de supprimer le listener lorsque le flow est annulé
        awaitClose { listenerRegistration.remove() }
    }

    /**
     * Récupère l'URL de la photo de profil de l'utilisateur.
     * @param userId L'ID de l'utilisateur.
     * @return L'URL de la photo de profil ou null si l'utilisateur n'a pas de photo de profil.
     */
    private suspend fun getUserProfilePictureUrl(userId: String): String? {
        val documentSnapshot = db.collection("users")
            .document(userId)
            .get()
            .await()
        return documentSnapshot.getString("pictureURL")
    }

    /**
     * Supprime un événement spécifique depuis Firestore.
     * @param eventId L'ID de l'événement à supprimer.
     */
    suspend fun deleteEvent(eventId: String) {
        db.collection("events")
            .document(eventId)
            .delete()
            .await()
    }

    /**
     * Récupère un événement spécifique par son ID.
     * @param eventId L'ID de l'événement à récupérer.
     * @return Un objet Event ou null si l'événement n'existe pas.
     */
    suspend fun getEventById(eventId: String): Event? {
        val document = db.collection("events")
            .document(eventId)
            .get()
            .await()

        val event = document.toObject(Event::class.java)

        return event?.let {
            val authorPictureUrl = getUserProfilePictureUrl(it.authorId)
            it.copy(authorPictureURL = authorPictureUrl)
        }
    }

    /**
     * Ajoute un participant à un événement.
     * @param eventId L'ID de l'événement.
     * @param userId L'ID de l'utilisateur à ajouter.
     */
    suspend fun addParticipant(eventId: String, userId: String) {
        val eventRef = db.collection("events").document(eventId)
        eventRef.update("participants", FieldValue.arrayUnion(userId)).await()
    }

    /**
     * Supprime un participant d'un événement.
     * @param eventId L'ID de l'événement.
     * @param userId L'ID de l'utilisateur à retirer.
     */
    suspend fun removeParticipant(eventId: String, userId: String) {
        val eventRef = db.collection("events").document(eventId)
        eventRef.update("participants", FieldValue.arrayRemove(userId)).await()
    }



}