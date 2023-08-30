package com.suonk.notepad_plus.ui.auth

import androidx.lifecycle.ViewModel
import com.suonk.notepad_plus.domain.use_cases.user.AddUserToFirestoreUseCase
import com.suonk.notepad_plus.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val addUserToFirestoreUseCase: AddUserToFirestoreUseCase) : ViewModel() {

    val isFieldsCorrectSingleLiveEvent = SingleLiveEvent<Boolean>()
    val toastMessageSingleLiveEvent = SingleLiveEvent<String>()

    fun checkIfFieldsAreCorrect(mail: String?, password: String?) {
        if (mail == null || password == null || mail.isEmpty() || password.isEmpty()) {
            isFieldsCorrectSingleLiveEvent.setValue(false)
            toastMessageSingleLiveEvent.setValue("Fields aren't correctly filled")
        } else {
            isFieldsCorrectSingleLiveEvent.setValue(true)
        }
    }

    fun addUserToFirestore() {
        addUserToFirestoreUseCase.invoke()
    }
}