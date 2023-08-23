package com.suonk.notepad_plus.domain.note.search

import com.suonk.notepad_plus.domain.repositories.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSearchNoteUseCase @Inject constructor(private val searchRepository: SearchRepository) {

    fun invoke() : Flow<String> = searchRepository.getCurrentSearchParametersFlow()
}