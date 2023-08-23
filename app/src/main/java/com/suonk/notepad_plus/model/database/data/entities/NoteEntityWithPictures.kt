package com.suonk.mynotepad.model.database.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import com.suonk.notepad_plus.model.database.data.entities.PictureEntity

@Entity
data class NoteEntityWithPictures(
    @Embedded
    val noteEntity: NoteEntity,
    @Relation(entity = PictureEntity::class, parentColumn = "id", entityColumn = "noteId")
    val photos: List<PictureEntity>
)