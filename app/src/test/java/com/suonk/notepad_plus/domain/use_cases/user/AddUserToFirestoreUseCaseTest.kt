package com.suonk.notepad_plus.domain.use_cases.user

import com.suonk.notepad_plus.firebase.user.CustomFirebaseUserRepository
import com.suonk.notepad_plus.firebase.user.FirebaseUsersRepository
import com.suonk.notepad_plus.model.database.data.entities.CustomFirebaseUser
import com.suonk.notepad_plus.utils.TestCoroutineRule
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class AddUserToFirestoreUseCaseTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    private val usersRepository: FirebaseUsersRepository = mockk()
    private val customFirebaseUserRepository: CustomFirebaseUserRepository = mockk()

    private val addUserToFirestoreUseCase = AddUserToFirestoreUseCase(usersRepository, customFirebaseUserRepository)

    @Before
    fun setup() {
    }

    @Test
    fun `initial case`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { customFirebaseUserRepository.getCustomFirebaseUser() } returns getCustomFirebaseUser()
        coJustRun { usersRepository.addNewUserToFirestore(DEFAULT_ID, getCustomFirebaseUser()) }

        // WHEN
        addUserToFirestoreUseCase.invoke()

        // THEN
        coVerify {
            customFirebaseUserRepository.getCustomFirebaseUser()
            usersRepository.addNewUserToFirestore(DEFAULT_ID, getCustomFirebaseUser())
        }
        confirmVerified(customFirebaseUserRepository, usersRepository)
    }

    @Test
    fun `case if id is null`() = testCoroutineRule.runTest {
        // GIVEN
        coEvery { customFirebaseUserRepository.getCustomFirebaseUser() } returns getCustomFirebaseUserWithNullId()

        // WHEN
        addUserToFirestoreUseCase.invoke()

        // THEN
        coVerify {
            customFirebaseUserRepository.getCustomFirebaseUser()
        }
        confirmVerified(customFirebaseUserRepository, usersRepository)
    }

    private fun getCustomFirebaseUserWithNullId(): CustomFirebaseUser {
        return CustomFirebaseUser(
            id = DEFAULT_ID_NULL, name = DEFAULT_NAME, mail = DEFAULT_MAIL, photo = DEFAULT_PHOTO
        )
    }

    private fun getCustomFirebaseUser(): CustomFirebaseUser {
        return CustomFirebaseUser(
            id = DEFAULT_ID, name = DEFAULT_NAME, mail = DEFAULT_MAIL, photo = DEFAULT_PHOTO
        )
    }

    companion object {
        private val DEFAULT_ID_NULL = null
        private const val DEFAULT_ID = "DEFAULT_ID"
        private const val DEFAULT_NAME = "DEFAULT_NAME"
        private const val DEFAULT_MAIL = "DEFAULT_MAIL"
        private const val DEFAULT_PHOTO = "DEFAULT_PHOTO"
    }
}