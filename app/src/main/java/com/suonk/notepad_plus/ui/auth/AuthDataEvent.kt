package com.suonk.notepad_plus.ui.auth

sealed class AuthDataEvent {
    data class ChangeEmail(val email: String) : AuthDataEvent()
    data class ChangePassword(val password: String) : AuthDataEvent()
    data class ChangeRememberFields(val isChecked: Boolean) : AuthDataEvent()
    data class AnimationIsFinished(val isReady: Boolean) : AuthDataEvent()
    data class LoginClick(val email: String, val password: String) : AuthDataEvent()
}