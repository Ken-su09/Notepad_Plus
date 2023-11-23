package com.suonk.notepad_plus.model.repositories

import android.util.Log
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.domain.repositories.SearchRepository
import com.suonk.notepad_plus.utils.Sorting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor() : SearchRepository {

    private val currentSearchParameterFlow = MutableStateFlow<String?>(null)
    private val currentSortFilterParameterFlow = MutableStateFlow(0)
    private val currentSortParameterFlow = MutableStateFlow(Sorting.DATE_ASC)

    override fun getCurrentSearchParametersFlow(): StateFlow<String?> = currentSearchParameterFlow

    override fun setCurrentSearchParameters(search: String?) {
        currentSearchParameterFlow.tryEmit(search)
    }

    override fun getCurrentSortFilterParametersFlow(): StateFlow<Int> {
        return currentSortFilterParameterFlow
    }

    override fun getCurrentSortParameterFlow(): StateFlow<Sorting> {
        return currentSortParameterFlow
    }

    override fun setCurrentSortFilterParametersFlow(itemId: Int) {
        Log.i("SortNote", "repository itemId : ${itemId}")
        when (itemId) {
            R.string.date_asc -> currentSortParameterFlow.tryEmit(Sorting.DATE_ASC)
            R.string.date_desc -> currentSortParameterFlow.tryEmit(Sorting.DATE_DESC)
            R.string.title_asc -> currentSortParameterFlow.tryEmit(Sorting.TITLE_ASC)
            R.string.title_desc -> currentSortParameterFlow.tryEmit(Sorting.TITLE_DESC)
            R.string.content_a_z -> currentSortParameterFlow.tryEmit(Sorting.CONTENT_ASC)
            R.string.content_z_a -> currentSortParameterFlow.tryEmit(Sorting.CONTENT_DESC)
            R.string.by_color -> currentSortParameterFlow.tryEmit(Sorting.COLOR_ASC)

            R.string.pink -> currentSortFilterParameterFlow.tryEmit(R.string.pink)
            R.string.purple -> currentSortFilterParameterFlow.tryEmit(R.string.purple)
            R.string.green -> currentSortFilterParameterFlow.tryEmit(R.string.green)
            R.string.blue -> currentSortFilterParameterFlow.tryEmit(R.string.blue)
            R.string.orange -> currentSortFilterParameterFlow.tryEmit(R.string.orange)
            R.string.yellow -> currentSortFilterParameterFlow.tryEmit(R.string.yellow)
            else -> {
                currentSortFilterParameterFlow.tryEmit(R.string.remove_filter)
            }
        }
    }

//    override fun addFilter(filter: Filter) {
//        currentMoreCriteriaFilterParameterFlow.update { filters ->
//            filters + filter
//        }
//    }
//
//    override fun removeFilter(filter: Filter) {
//        currentMoreCriteriaFilterParameterFlow.update { filters ->
//            filters - filter
//        }
//    }
}