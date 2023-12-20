package com.suonk.notepad_plus.di

import com.suonk.notepad_plus.domain.filter.FilterRepository
import com.suonk.notepad_plus.domain.filter.FilterRepositoryImpl
import com.suonk.notepad_plus.domain.note.id.CurrentNoteIdRepository
import com.suonk.notepad_plus.domain.note.get_note.NoteRepository
import com.suonk.notepad_plus.domain.note.id.CurrentNoteIdRepositoryImpl
import com.suonk.notepad_plus.domain.search.SearchRepository
import com.suonk.notepad_plus.firebase.user.FirebaseUsersRepository
import com.suonk.notepad_plus.firebase.user.FirebaseUsersRepositoryImpl
import com.suonk.notepad_plus.domain.note.get_note.NoteRepositoryImpl
import com.suonk.notepad_plus.domain.search.SearchRepositoryImpl
import com.suonk.notepad_plus.domain.sort.SortRepository
import com.suonk.notepad_plus.domain.sort.SortRepositoryImpl
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
    abstract fun bindFilterRepository(impl: FilterRepositoryImpl): FilterRepository

    @Binds
    @Singleton
    abstract fun bindSortRepository(impl: SortRepositoryImpl): SortRepository

    @Binds
    @Singleton
    abstract fun bindCurrentNoteIdRepositoryImpl(impl: CurrentNoteIdRepositoryImpl): CurrentNoteIdRepository

    @Binds
    @Singleton
    abstract fun bindUsersRepositoryImpl(impl: FirebaseUsersRepositoryImpl): FirebaseUsersRepository
}