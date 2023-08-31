package com.suonk.notepad_plus.ui.note.details

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.databinding.FragmentNoteDetailsBinding
import com.suonk.notepad_plus.ui.main.MainActivity
import com.suonk.notepad_plus.utils.showToast
import com.suonk.notepad_plus.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailsFragment : Fragment(R.layout.fragment_note_details) {

    private val viewModel by viewModels<NoteDetailsViewModel>()
    private val binding by viewBinding(FragmentNoteDetailsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        bindDataFromViewModelToView()

        viewModel.finishSavingSingleLiveEvent.observe(viewLifecycleOwner) {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }
        viewModel.toastMessageSingleLiveEvent.observe(viewLifecycleOwner) {
            it.showToast(requireContext())
        }
    }

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.details_toolbar)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_delete_note -> {
                }
                R.id.action_save_note -> {
                    viewModel.onSaveNoteMenuItemClicked(
                        title = binding.title.text.toString(),
                        content = binding.content.text.toString()
                    )
                }
                else -> {
                    it.isChecked = true
                }
            }
            true
        }
    }

    private fun bindDataFromViewModelToView() {
        viewModel.noteDetailsViewState.observe(viewLifecycleOwner) { noteDetails ->
            binding.title.setText(noteDetails.title)
            binding.content.setText(noteDetails.content)
            binding.date.text = noteDetails.dateText.toString()
        }
    }
}