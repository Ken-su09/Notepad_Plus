package com.suonk.notepad_plus.model.database.data.dao

import androidx.room.*
import com.suonk.notepad_plus.model.database.data.entities.PictureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PictureDao {

    //region ================================================================= GET ==================================================================

    @Transaction
    @Query("SELECT * FROM picture_entity WHERE noteId = :id")
    fun getListOfPicturesByNoteId(id: Long): Flow<List<PictureEntity>>

    //endregion

    //region ================================================================ UPSERT ================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewPicture(pictureEntity: PictureEntity)

    //endregion

    //region ================================================================ UPSERT ================================================================

    @Delete
    suspend fun deletePicture(pictureEntity: PictureEntity)

    @Transaction
    @Query("DELETE FROM picture_entity WHERE id = :id")
    suspend fun deletePictureWithId(id: Long)

    //endregion
}