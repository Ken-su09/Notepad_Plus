package com.suonk.notepad_plus.ui.note.details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.databinding.ActivityNoteDetailsBinding
import com.suonk.notepad_plus.ui.note.list.NotesListFragment
import com.suonk.notepad_plus.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailsActivity : AppCompatActivity() {

    private val binding by viewBinding { ActivityNoteDetailsBinding.inflate(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.details_fragment_container, NoteDetailsFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}