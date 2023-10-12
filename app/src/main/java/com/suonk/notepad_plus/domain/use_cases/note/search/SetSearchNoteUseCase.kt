package com.suonk.notepad_plus.domain.use_cases.note.search

import com.suonk.notepad_plus.domain.repositories.SearchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetSearchNoteUseCase @Inject constructor(private val searchRepository: SearchRepository) {

    fun invoke(search: String?) {
        searchRepository.setCurrentSearchParameters(search)
    }
}