package com.suonk.notepad_plus.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.suonk.notepad_plus.ui.note.list.NotesListFragment
import com.suonk.mynotepad.utils.viewBinding
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding { ActivityMainBinding.inflate(it) }
//    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_view, NotesListFragment(), "")
            .addToBackStack(null)
            .commit()
    }
}