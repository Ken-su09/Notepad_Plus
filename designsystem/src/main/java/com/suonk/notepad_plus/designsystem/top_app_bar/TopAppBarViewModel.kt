package com.suonk.notepad_plus.designsystem.top_app_bar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TopAppBarViewModel constructor() : ViewModel() {

    private val _searchBarText = MutableStateFlow("")
    val searchBarText = _searchBarText.asStateFlow()

    fun setSearchParameters(search: String?) {
        search?.let {
            _searchBarText.value = it
        }
    }
}