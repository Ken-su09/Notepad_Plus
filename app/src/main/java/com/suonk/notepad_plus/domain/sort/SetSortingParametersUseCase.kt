package com.suonk.notepad_plus.domain.sort

import com.suonk.notepad_plus.utils.Sorting
import javax.inject.Inject

class SetSortingParametersUseCase @Inject constructor(private val sortRepository: SortRepository) {
    fun invoke(sortFilterParameter: Sorting) {
        sortRepository.setCurrentSortingParametersFlow(sortFilterParameter)
    }
}