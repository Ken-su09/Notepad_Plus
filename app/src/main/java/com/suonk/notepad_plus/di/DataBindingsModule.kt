package com.suonk.notepad_plus.di

import com.suonk.notepad_plus.domain.filter.FilterRepository
import com.suonk.notepad_plus.domain.filter.FilterRepositoryImpl
import com.suonk.notepad_plus.domain.note.delete_note.DeleteNoteRepository
import com.suonk.notepad_plus.domain.note.delete_note.DeleteNoteRepositoryImpl
import com.suonk.notepad_plus.domain.note.id.CurrentNoteIdRepository
import com.suonk.notepad_plus.domain.note.get_note.GetNotesRepository
import com.suonk.notepad_plus.domain.note.id.CurrentNoteIdRepositoryImpl
import com.suonk.notepad_plus.domain.search.SearchRepository
import com.suonk.notepad_plus.firebase.user.FirebaseUsersRepository
import com.suonk.notepad_plus.firebase.user.FirebaseUsersRepositoryImpl
import com.suonk.notepad_plus.domain.note.get_note.GetNotesRepositoryImpl
import com.suonk.notepad_plus.domain.note.upsert.UpsertNoteRepository
import com.suonk.notepad_plus.domain.note.upsert.UpsertNoteRepositoryImpl
import com.suonk.notepad_plus.domain.search.SearchRepositoryImpl
import com.suonk.notepad_plus.domain.shared_preferences.RememberFieldsRepository
import com.suonk.notepad_plus.domain.shared_preferences.RememberFieldsRepositoryImpl
import com.suonk.notepad_plus.domain.sort.SortRepository
import com.suonk.notepad_plus.domain.sort.SortRepositoryImpl
import com.suonk.notepad_plus.firebase.notes.FirebaseNotesRepository
import com.suonk.notepad_plus.firebase.notes.FirebaseNotesRepositoryImpl
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
    abstract fun bindGetNotesRepository(impl: GetNotesRepositoryImpl): GetNotesRepository

    @Binds
    @Singleton
    abstract fun bindDeleteNoteRepository(impl: DeleteNoteRepositoryImpl): DeleteNoteRepository

    @Binds
    @Singleton
    abstract fun bindUpsertNoteRepository(impl: UpsertNoteRepositoryImpl): UpsertNoteRepository

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
    abstract fun bindFirebaseUsersRepository(impl: FirebaseUsersRepositoryImpl): FirebaseUsersRepository

    @Binds
    @Singleton
    abstract fun bindFirebaseNotesRepositoryImpl(impl: FirebaseNotesRepositoryImpl): FirebaseNotesRepository

    @Binds
    @Singleton
    abstract fun bindRememberFieldsRepositoryImpl(impl: RememberFieldsRepositoryImpl): RememberFieldsRepository
}