package com.suonk.notepad_plus.domain.use_cases.note.filter_sort

import com.suonk.notepad_plus.domain.repositories.SearchRepository
import com.suonk.notepad_plus.utils.Sorting
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSortingParametersUseCase @Inject constructor(private val searchRepository: SearchRepository) {

    fun invoke(): Flow<Sorting> {
        return searchRepository.getCurrentSortParameterFlow()
    }
}