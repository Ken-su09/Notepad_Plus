package com.suonk.notepad_plus.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.databinding.ActivityMainBinding
import com.suonk.notepad_plus.ui.auth.AuthActivity
import com.suonk.notepad_plus.ui.note.details.NoteDetailsActivity
import com.suonk.notepad_plus.ui.note.list.NotesListFragment
import com.suonk.notepad_plus.utils.Event.Companion.observeEvent
import com.suonk.notepad_plus.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding { ActivityMainBinding.inflate(it) }
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        supportFragmentManager.commit { replace(R.id.fragment_container_view, NotesListFragment(), null) }

        viewModel.mainViewAction.observeEvent(this) { action ->
            when (action) {
                is MainViewAction.Navigate.Detail -> {
                    Log.i("GET_NOTE", "action : $action")
                    startActivity(Intent(this@MainActivity, NoteDetailsActivity::class.java))
                }
            }
        }
    }
}