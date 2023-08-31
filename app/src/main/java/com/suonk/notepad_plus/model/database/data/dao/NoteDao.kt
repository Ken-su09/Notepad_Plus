package com.suonk.notepad_plus.model.database.data.dao

import androidx.room.*
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.model.database.data.entities.NoteEntityWithPictures
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    //region ================================================================= GET ==================================================================

    @Transaction
    @Query("SELECT * FROM note_entity WHERE isDeleted == 0 ORDER BY id ASC")
    fun getAllNotesWithPictures(): Flow<List<NoteEntityWithPictures>>

    @Transaction
    @Query("SELECT * FROM note_entity WHERE isDeleted == 1 ORDER BY id ASC")
    fun getAllDeletedNotesWithPictures(): Flow<List<NoteEntityWithPictures>>

    @Transaction
    @Query("SELECT * FROM note_entity WHERE id == :id")
    fun getNoteById(id: Long): Flow<NoteEntityWithPictures>

    //endregion

    //region ============================================================= INSERT/UPDATE ============================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertNoteEntity(noteEntity: NoteEntity): Long

    //endregion

    //region ================================================================ DELETE ================================================================

    @Delete
    suspend fun deleteNoteEntity(noteEntity: NoteEntity)

    @Query("DELETE from note_entity WHERE id = :id")
    suspend fun deleteNoteEntityById(id: Long)

    //endregion
}