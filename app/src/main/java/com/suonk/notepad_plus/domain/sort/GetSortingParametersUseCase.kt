package com.suonk.notepad_plus.domain.sort

import com.suonk.notepad_plus.utils.Sorting
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSortingParametersUseCase @Inject constructor(private val sortRepository: SortRepository) {

    fun invoke(): Flow<Sorting> {
        return sortRepository.getCurrentSortParameterFlow()
    }
}