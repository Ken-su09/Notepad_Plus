package com.suonk.notepad_plus.domain.use_cases.note.filter_sort

import android.util.Log
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.domain.repositories.SearchRepository
import javax.inject.Inject

class SetCurrentSortFilterUseCase @Inject constructor(private val searchRepository: SearchRepository) {

    fun invoke(sortFilterParameter: Int) {
        Log.i("SortNote", "use case sortFilterParameter : ${sortFilterParameter}")
        searchRepository.setCurrentSortFilterParametersFlow(sortFilterParameter)
    }
}