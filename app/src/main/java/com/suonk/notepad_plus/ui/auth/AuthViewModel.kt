package com.suonk.notepad_plus.ui.auth

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.domain.shared_preferences.GetRememberFieldsUseCase
import com.suonk.notepad_plus.domain.shared_preferences.SetRememberFieldsUseCase
import com.suonk.notepad_plus.domain.use_cases.user.AddUserToFirestoreUseCase
import com.suonk.notepad_plus.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val addUserToFirestoreUseCase: AddUserToFirestoreUseCase,

    getRememberFieldsUseCase: GetRememberFieldsUseCase,
    private val setRememberFieldsUseCase: SetRememberFieldsUseCase,
) : ViewModel() {

    private val _isReadyFlow = MutableStateFlow(false)
    val isReadyFlow = _isReadyFlow.asStateFlow()

    private val _emailValueFlow = MutableStateFlow("")
    val emailValueFlow = _emailValueFlow.asStateFlow()

    private val _emailIconValidationValueFlow = MutableStateFlow(R.drawable.ic_check_email_cross)
    val emailIconValidationValueFlow = _emailIconValidationValueFlow.asSharedFlow()

    private val _emailIconValidationColorValueFlow = MutableStateFlow(Color.Red)
    val emailIconValidationColorValueFlow = _emailIconValidationColorValueFlow.asSharedFlow()

    private val _passwordValueFlow = MutableStateFlow("")
    val passwordValueFlow = _passwordValueFlow.asSharedFlow()

    private val _passwordAnimationValueFlow = MutableStateFlow(R.drawable.password_visible_animation)
    val passwordAnimationValueFlow = _passwordAnimationValueFlow.asSharedFlow()

    private val _rememberFieldsValueFlow = MutableStateFlow(getRememberFieldsUseCase.invoke())
    val rememberFieldsValueFlow = _rememberFieldsValueFlow.asSharedFlow()

    private val _authUiEvent = MutableSharedFlow<AuthUiEvent>()
    val authUiEvent = _authUiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            delay(3000L)
            _isReadyFlow.value = true
        }
    }

//    fun checkIfFieldsAreCorrect(mail: String?, password: String?) {
//        if (mail == null || password == null || mail.isEmpty() || password.isEmpty()) {
//            isFieldsCorrectSingleLiveEvent.setValue(false)
//            toastMessageSingleLiveEvent.setValue("Fields aren't correctly filled")
//        } else {
//            isFieldsCorrectSingleLiveEvent.setValue(true)
//        }
//    }

    fun addUserToFirestore() {
        addUserToFirestoreUseCase.invoke()

        viewModelScope.launch {
            _authUiEvent.emit(AuthUiEvent.LoginSuccessful)
        }
    }

    fun onEvent(authDataEvent: AuthDataEvent) {
        when (authDataEvent) {
            is AuthDataEvent.ChangeEmail -> {
                _emailValueFlow.value = authDataEvent.email

                val emailPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")

                if (authDataEvent.email.trim().matches(emailPattern.toRegex())) {
                    _emailIconValidationValueFlow.value = R.drawable.ic_check_email
                    _emailIconValidationColorValueFlow.value = Color.Green
                } else {
                    _emailIconValidationValueFlow.value = R.drawable.ic_check_email_cross
                    _emailIconValidationColorValueFlow.value = Color.Red
                }
            }

            is AuthDataEvent.ChangePassword -> {
                _passwordValueFlow.value = authDataEvent.password
            }

            is AuthDataEvent.ChangeRememberFields -> {
                _rememberFieldsValueFlow.value = authDataEvent.isChecked
                setRememberFieldsUseCase.invoke(authDataEvent.isChecked)
            }

            is AuthDataEvent.LoginClick -> {
//                if (authDataEvent.email.isEmpty() || authDataEvent.password.isEmpty()) {
//                    binding.loginEmailValidation.visibility = View.INVISIBLE
//                } else {
//                    binding.loginEmailValidation.visibility = View.VISIBLE
//                    binding.loginEmailValidation.setImageDrawable(
//                        AppCompatResources.getDrawable(
//                            this,
//                            R.drawable.ic_check_email_cross
//                        )
//                    )
//                }
            }
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
}