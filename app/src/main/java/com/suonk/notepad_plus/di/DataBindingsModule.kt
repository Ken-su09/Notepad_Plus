package com.suonk.notepad_plus.di

import com.suonk.mynotepad.domain.repositories.CurrentNoteIdRepository
import com.suonk.mynotepad.domain.repositories.NoteRepository
import com.suonk.mynotepad.model.database.data.repositories.CurrentNoteIdRepositoryImpl
import com.suonk.notepad_plus.domain.repositories.SearchRepository
import com.suonk.notepad_plus.model.repositories.NoteRepositoryImpl
import com.suonk.notepad_plus.model.repositories.SearchRepositoryImpl
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
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Binds
    @Singleton
    abstract fun bindCurrentNoteIdRepositoryImpl(impl: CurrentNoteIdRepositoryImpl): CurrentNoteIdRepository
}