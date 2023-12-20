package com.suonk.notepad_plus.domain.filter

import com.suonk.notepad_plus.utils.Filtering
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilterParametersUseCase @Inject constructor(private val filterRepository: FilterRepository) {

    fun invoke(): Flow<Filtering> {
        return filterRepository.getCurrentFilterParameterFlow()
    }
}