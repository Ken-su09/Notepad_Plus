package com.suonk.notepad_plus.ui.auth

import androidx.lifecycle.ViewModel
import com.suonk.notepad_plus.domain.use_cases.user.AddUserToFirestoreUseCase
import com.suonk.notepad_plus.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.regex.Pattern
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

//    private fun checkEmailValidationSignUp(): Boolean {
//        val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
//        val userEmail = binding.loginEmail.text.toString()
//
//        return userEmail.trim().matches(emailPattern.toRegex())
//    }

    private fun checkEmailConstantly(mail : String) {
        val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

        if (mail.trim().matches(emailPattern.toRegex())) {

        } else {
//            if (userEmail.isEmpty()) {
//                binding.loginEmailValidation.visibility = View.INVISIBLE
//            } else {
//                binding.loginEmailValidation.visibility = View.VISIBLE
//                binding.loginEmailValidation.setImageDrawable(
//                    AppCompatResources.getDrawable(
//                        this,
//                        R.drawable.ic_check_email_cross
//                    )
//                )
//            }
        }
    }

    fun onLoginClicked(email: String, password: String) {

    }
}