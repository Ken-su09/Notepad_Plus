package com.suonk.mynotepad.model.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.suonk.mynotepad.model.database.data.entities.PictureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PictureDao {

    //region ================================================================= GET ==================================================================

    @Transaction
    @Query("SELECT * FROM picture_entity WHERE realEstateId = :id")
    fun getListOfPicturesByNoteId(id: Long): Flow<List<PictureEntity>>

    //endregion
}