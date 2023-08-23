package com.suonk.mynotepad.model.database.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "note_entity")
data class NoteEntity(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0L,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "content") var content: String,
    @ColumnInfo(name = "date") var date: LocalDateTime,
    @ColumnInfo(name = "color") var color: String,
    @ColumnInfo(name = "isFavorite") var isFavorite: Int,
    @ColumnInfo(name = "isDeleted") var isDeleted: Int
)