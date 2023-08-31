package com.suonk.notepad_plus.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.suonk.notepad_plus.model.database.AppDatabase
import com.suonk.notepad_plus.model.database.data.dao.NoteDao
import com.suonk.notepad_plus.model.database.data.entities.PictureEntity
import com.suonk.notepad_plus.model.database.data.dao.PictureDao
import com.suonk.notepad_plus.model.database.data.entities.NoteEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDateTime
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context, providerNoteDao: Provider<NoteDao>, providerPictureDao: Provider<PictureDao>
    ) = Room.databaseBuilder(context, AppDatabase::class.java, "app_database").allowMainThreadQueries()
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                prepopulateDatabase(providerNoteDao.get(), providerPictureDao.get())
            }

            private fun prepopulateDatabase(noteDao: NoteDao, pictureDao: PictureDao) {
                CoroutineScope(Dispatchers.IO).launch {
                    noteDao.upsertNoteEntity(
                        NoteEntity(
                            id = 1L,
                            title = "First News of the Week",
                            content = "Règle : \n" +
                                "- Nombre aléatoire en 10 - 255\n" +
                                "- Le faire 5 fois\n" +
                                "- Tirer 5 max mangas intéressants dans chaque page (peut aller page avant et après)\n" +
                                "- Pas le droit à la même page\n" +
                                "- Faire un tri entre les 5 ou prendre celui qui paraît être le plus omoshiroi",
                            date = LocalDateTime.now(),
                            color = 0,
                            isFavorite = false,
                            isDeleted = false
                        )
                    )
                }

            }
        }).addMigrations().build()

    @Provides
    @Singleton
    fun provideClock(): Clock = Clock.systemDefaultZone()

    @Provides
    @Singleton
    fun provideNoteDao(database: AppDatabase) = database.noteDao()

    @Provides
    @Singleton
    fun providePictureDao(database: AppDatabase) = database.pictureDao()

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()
}