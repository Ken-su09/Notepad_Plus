package com.suonk.notepad_plus.ui.auth

sealed class AuthUiEvent {
    object LoginSuccessful : AuthUiEvent()
}