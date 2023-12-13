package com.suonk.notepad_plus.firebase.user

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.suonk.notepad_plus.model.database.data.entities.CustomFirebaseUser
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test


class CustomFirebaseUserRepositoryTest {

    private val auth: FirebaseAuth = mockk()
    private val customFirebaseUserRepository = CustomFirebaseUserRepository(auth)

    @Before
    fun setup() {
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockk()
    }

//    @Test
//    fun `nominal case`() {
//        every { auth.currentUser } returns null
//
//        assertEquals(defaultCustomFirebaseUserWithNullData(), customFirebaseUserRepository.getCustomFirebaseUser())
//
//        verify { auth.currentUser }
//        confirmVerified(auth)
//    }

//    @Test
//    fun `test get custom firebase user if current user is null`() {
//        every { auth.currentUser } returns mockk {
//            every { uid } returns DEFAULT_ID
//            every { displayName } returns DEFAULT_NAME
//            every { email } returns DEFAULT_MAIL
//            every { photoUrl } returns Uri.parse(DEFAULT_PHOTO_URL)
//        }
//
//        assertEquals(defaultCustomFirebaseUser(), customFirebaseUserRepository.getCustomFirebaseUser())
//
//        verify { auth.currentUser }
//        confirmVerified(auth)
//    }

    private fun defaultCustomFirebaseUserWithNullData(): CustomFirebaseUser {
        return CustomFirebaseUser(
            id = null,
            name = null,
            mail = null,
            photo = null
        )
    }

    private fun defaultCustomFirebaseUser(): CustomFirebaseUser {
        return CustomFirebaseUser(
            id = DEFAULT_ID,
            name = DEFAULT_NAME,
            mail = DEFAULT_MAIL,
            photo = DEFAULT_PHOTO_URL
        )
    }

    companion object {
        private const val DEFAULT_ID = "DEFAULT_ID"
        private const val DEFAULT_NAME = "DEFAULT_NAME"
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
        private const val DEFAULT_PHOTO_URL = "DEFAULT_PHOTO_URL"
    }
}