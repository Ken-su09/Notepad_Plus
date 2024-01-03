package com.suonk.notepad_plus.domain.shared_preferences

import javax.inject.Inject

class GetRememberFieldsUseCase @Inject constructor(private val rememberFieldsRepository: RememberFieldsRepository) {

    fun invoke() = rememberFieldsRepository.getRememberFields()
}