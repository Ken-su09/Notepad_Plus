package com.suonk.notepad_plus.domain.shared_preferences

interface RememberFieldsRepository {

    fun getRememberFields(): Boolean

    fun setRememberFields(isChecked: Boolean)
}