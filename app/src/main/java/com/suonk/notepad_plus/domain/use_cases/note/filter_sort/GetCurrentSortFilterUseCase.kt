package com.suonk.notepad_plus.domain.use_cases.note.filter_sort

import com.suonk.notepad_plus.domain.repositories.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentSortFilterUseCase @Inject constructor(private val searchRepository: SearchRepository) {

    fun invoke(): Flow<Int> {
        return searchRepository.getCurrentSortFilterParametersFlow()
    }
}