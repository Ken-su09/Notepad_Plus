package com.suonk.notepad_plus.di

import com.suonk.mynotepad.domain.repositories.CurrentNoteIdRepository
import com.suonk.mynotepad.domain.repositories.NoteRepository
import com.suonk.mynotepad.model.database.data.repositories.CurrentNoteIdRepositoryImpl
import com.suonk.mynotepad.model.database.data.repositories.NoteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class DataBindingsModule {

    @Binds
    @Singleton
    abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

    @Binds
    @Singleton
    abstract fun bindCurrentRealEstateIdRepositoryImpl(impl: CurrentNoteIdRepositoryImpl): CurrentNoteIdRepository
}