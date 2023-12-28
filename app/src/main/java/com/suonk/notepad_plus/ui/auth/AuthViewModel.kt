package com.suonk.notepad_plus.ui.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.suonk.notepad_plus.domain.use_cases.user.AddUserToFirestoreUseCase
import com.suonk.notepad_plus.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val addUserToFirestoreUseCase: AddUserToFirestoreUseCase,
//    private val oneTapClient: SignInClient,
//    private val auth: FirebaseAuth
) : ViewModel() {

    val isFieldsCorrectSingleLiveEvent = SingleLiveEvent<Boolean>()
    val toastMessageSingleLiveEvent = SingleLiveEvent<String>()

    private val _emailValueFlow = MutableSharedFlow<String>()
    val emailValueFlow = _emailValueFlow.asSharedFlow()

    private val _passwordValueFlow = MutableSharedFlow<String>()
    val passwordValueFlow = _passwordValueFlow.asSharedFlow()

    private val _authUiEvent = MutableSharedFlow<AuthUiEvent>()
    val authUiEvent = _authUiEvent.asSharedFlow()

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

        viewModelScope.launch {
            _authUiEvent.emit(AuthUiEvent.LoginSuccessful)
        }
    }

    fun onEvent(authDataEvent: AuthDataEvent) {
        when (authDataEvent) {
            is AuthDataEvent.ChangeEmail -> {

            }

            is AuthDataEvent.ChangePassword -> {

            }

            AuthDataEvent.LoginClick -> {

            }
        }
    }

//    private fun checkEmailValidationSignUp(): Boolean {
//        val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
//        val userEmail = binding.loginEmail.text.toString()
//
//        return userEmail.trim().matches(emailPattern.toRegex())
//    }

    private fun checkEmailConstantly(mail: String) {
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

//    suspend fun signInWithIntent(intent: Intent) {
//        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
//        val googleIdToken = credential.googleIdToken
//        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
//
//        val user = auth.signInWithCredential(googleCredentials).await().user
//
//        addUserToFirestoreUseCase.invoke()
//    }
//
//    suspend fun signOut() {
//        oneTapClient.signOut().await()
//        auth.signOut()
//    }

    fun onLoginClickedWithMailAndPassword(email: String, password: String) {

    }

    fun onLoginClickedWithGoogle() {

    }
}