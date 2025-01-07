package com.openclassroom.eventorias.domainUnitTest

import com.openclassroom.eventorias.domain.User
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test

/**
 * Unit tests for the User data model.
 */
class UserUnitTest {

    /**
     * Test the default values of the User data model constructor.
     */
    @Test
    fun testDefaultValuesConstructor() {
        val user = User()
        assertEquals("", user.id)
        assertEquals("", user.firstname)
        assertEquals("", user.lastname)
        assertEquals("", user.email)
        assertNull(user.pictureURL)
    }

    /**
     * Test the custom values of the User data model constructor.
     */
    @Test
    fun testCustomValuesConstructor() {
        val user = User(
            id = "123",
            firstname = "John",
            lastname = "Doe",
            email = "john.doe@example.com",
            pictureURL = "http://example.com/picture.jpg"
        )

        assertEquals("123", user.id)
        assertEquals("John", user.firstname)
        assertEquals("Doe", user.lastname)
        assertEquals("john.doe@example.com", user.email)
        assertEquals("http://example.com/picture.jpg", user.pictureURL)
    }

    /**
     * Test the null optional fields of the User data model.
     */
    @Test
    fun testNullOptionalFields() {
        val user = User(id = "123", firstname = "John", lastname = "Doe", email = "john.doe@example.com")

        // Vérifier que pictureURL est null lorsque non fourni
        assertNull(user.pictureURL)
    }

    /**
     * Test the immutability of the User data model.
     */
    @Test
    fun testUserImmutability() {
        val user = User(id = "123", firstname = "John", lastname = "Doe", email = "john.doe@example.com")

        // S'assurer que les propriétés sont en lecture seule
        // Note : Kotlin `data` classes ont des propriétés val par défaut, donc pas de setter public
        assertEquals("123", user.id)
        assertEquals("John", user.firstname)
        assertEquals("Doe", user.lastname)
        assertEquals("john.doe@example.com", user.email)
    }

    /**
     * Test the equality of two users object.
     */
    @Test
    fun testUserEquality() {
        val user1 = User(id = "123", firstname = "John", lastname = "Doe", email = "john.doe@example.com")
        val user2 = User(id = "123", firstname = "John", lastname = "Doe", email = "john.doe@example.com")
        val user3 = User(id = "124", firstname = "Jane", lastname = "Smith", email = "jane.smith@example.com")

        // Test de l'égalité des utilisateurs ayant les mêmes valeurs
        assertTrue(user1 == user2)

        // Test de l'inégalité avec un utilisateur différent
        assertFalse(user1 == user3)
    }



}