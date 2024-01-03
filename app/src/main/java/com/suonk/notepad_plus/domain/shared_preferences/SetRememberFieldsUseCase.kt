package com.suonk.notepad_plus.domain.shared_preferences

import javax.inject.Inject

class SetRememberFieldsUseCase @Inject constructor(private val rememberFieldsRepository: RememberFieldsRepository) {

    fun invoke(isChecked: Boolean) {
        rememberFieldsRepository.setRememberFields(isChecked)
    }
}