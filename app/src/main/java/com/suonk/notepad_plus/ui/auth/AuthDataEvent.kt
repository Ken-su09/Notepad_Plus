package com.suonk.notepad_plus.ui.auth

sealed class AuthDataEvent {
    data class ChangeEmail(val email: String) : AuthDataEvent()
    data class ChangePassword(val password: String) : AuthDataEvent()
    object LoginClick : AuthDataEvent()
}