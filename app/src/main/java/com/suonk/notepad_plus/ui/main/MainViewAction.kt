package com.suonk.notepad_plus.ui.main

sealed class MainViewAction {
    sealed class Navigate : MainViewAction() {
        data class Detail(val noteId: Long) : Navigate()
    }
}