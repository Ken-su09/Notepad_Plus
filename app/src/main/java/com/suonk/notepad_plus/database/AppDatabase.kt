package com.suonk.mynotepad.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.suonk.mynotepad.model.database.dao.NoteDao
import com.suonk.mynotepad.model.database.dao.PictureDao
import com.suonk.mynotepad.model.database.data.entities.NoteEntity
import com.suonk.mynotepad.model.database.data.entities.PictureEntity
import com.suonk.mynotepad.utils.Converters

@Database(entities = [NoteEntity::class, PictureEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun pictureDao(): PictureDao
}