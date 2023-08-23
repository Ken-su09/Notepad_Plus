package com.suonk.notepad_plus.model.database.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "picture_entity")
data class PictureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val noteId: Long,
    val picture: String
)