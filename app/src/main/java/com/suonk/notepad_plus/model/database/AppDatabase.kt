package com.suonk.notepad_plus.model.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.suonk.notepad_plus.model.database.data.dao.NoteDao
import com.suonk.notepad_plus.model.database.data.dao.PictureDao
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import com.suonk.notepad_plus.model.database.data.entities.PictureEntity
import com.suonk.notepad_plus.utils.Converters

@Database(entities = [NoteEntity::class, PictureEntity::class], version = 1, autoMigrations = [AutoMigration(from = 1, to = 2)], exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun pictureDao(): PictureDao
}