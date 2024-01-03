package com.suonk.notepad_plus.domain.shared_preferences

import android.app.Application
import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RememberFieldsRepositoryImpl @Inject constructor(private val application: Application) : RememberFieldsRepository {

    override fun getRememberFields(): Boolean {
        return application.getSharedPreferences(REMEMBER_FIELDS_CHECKED, Context.MODE_PRIVATE).getBoolean(REMEMBER_FIELDS_CHECKED, false)
    }

    override fun setRememberFields(isChecked: Boolean) {
        val editor = application.getSharedPreferences(REMEMBER_FIELDS_CHECKED, Context.MODE_PRIVATE).edit()
        editor.putBoolean(REMEMBER_FIELDS_CHECKED, isChecked)
        editor.apply()
    }

    companion object {
        const val REMEMBER_FIELDS_CHECKED = "REMEMBER_FIELDS_CHECKED"
    }
}