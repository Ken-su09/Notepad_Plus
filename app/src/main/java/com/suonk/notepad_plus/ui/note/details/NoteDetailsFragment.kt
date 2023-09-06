package com.suonk.notepad_plus.ui.note.details

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.databinding.FragmentNoteDetailsBinding
import com.suonk.notepad_plus.ui.main.MainActivity
import com.suonk.notepad_plus.ui.note.details.actions_list.ActionsListAdapter
import com.suonk.notepad_plus.utils.showToast
import com.suonk.notepad_plus.utils.toCharSequence
import com.suonk.notepad_plus.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteDetailsFragment : Fragment(R.layout.fragment_note_details) {

    private val viewModel by viewModels<NoteDetailsViewModel>()
    private val binding by viewBinding(FragmentNoteDetailsBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()

        binding.editor.background = ResourcesCompat.getDrawable(resources, R.drawable.custom_edit_text_background, null)
        binding.editor.setPlaceholder(getString(R.string.content_hint))

        bindDataFromViewModelToView()

        viewModel.finishSavingSingleLiveEvent.observe(viewLifecycleOwner) {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }
        viewModel.toastMessageSingleLiveEvent.observe(viewLifecycleOwner) {
            it.showToast(requireContext())
        }
        viewModel.editorActionsSingleLiveEvent.observe(viewLifecycleOwner) {
            Log.i("RichEditor", "it : $it")
            when (it) {
                R.drawable.ic_undo -> {
                    binding.editor.undo()
                }

                R.drawable.ic_redo -> {
                    binding.editor.redo()
                }

                R.drawable.ic_bold -> {
                    binding.editor.setBold()
                }

                R.drawable.ic_italic -> {
                    binding.editor.setItalic()
                }

                else -> {

                }
            }
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
                        title = binding.title.text.toString(), content = "binding.content.title.toString()"
//                        content = binding.content.title.toString()
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
            binding.editor.html = noteDetails.content
            binding.date.text = noteDetails.dateText.toCharSequence(requireContext())

            val adapter = ActionsListAdapter()
            adapter.submitList(noteDetails.actions)
            binding.recyclerView.adapter = adapter
        }
    }
}