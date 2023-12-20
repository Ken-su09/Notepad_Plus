package com.suonk.notepad_plus.domain.filter

import com.suonk.notepad_plus.domain.search.SearchRepository
import com.suonk.notepad_plus.utils.Sorting
import javax.inject.Inject

class SetCurrentSortFilterUseCase @Inject constructor(private val searchRepository: SearchRepository) {

    fun invoke(sortFilterParameter: Sorting) {
        searchRepository.setCurrentSortFilterParametersFlow(sortFilterParameter)
    }
}