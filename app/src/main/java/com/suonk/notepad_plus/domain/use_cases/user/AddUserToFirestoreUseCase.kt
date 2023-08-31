package com.suonk.notepad_plus.domain.use_cases.user

import com.suonk.notepad_plus.firebase.user.CustomFirebaseUserRepository
import com.suonk.notepad_plus.firebase.user.FirebaseUsersRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddUserToFirestoreUseCase @Inject constructor(
    private val usersRepository: FirebaseUsersRepository, private val customFirebaseUserRepository: CustomFirebaseUserRepository
) {

    fun invoke() {
        customFirebaseUserRepository.getCustomFirebaseUser().id?.let {
            usersRepository.addNewUserToFirestore(it, customFirebaseUserRepository.getCustomFirebaseUser())
        }
    }
}