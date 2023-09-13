package com.suonk.notepad_plus.ui.note.details

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.databinding.FragmentNoteDetailsBinding
import com.suonk.notepad_plus.databinding.InsertAlertDialogLayoutBinding
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
        setHasOptionsMenu(true)
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
        binding.editor.setFontSize(16)
        viewModel.editorActionsSingleLiveEvent.observe(viewLifecycleOwner) {
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

                R.drawable.ic_x2_subscript -> {
                    binding.editor.setSubscript()
                }

                R.drawable.ic_x2_squared -> {
                    binding.editor.setSuperscript()
                }

                R.drawable.ic_heading_1 -> {
                    binding.editor.setHeading(1)
                }

                R.drawable.ic_heading_2 -> {
                    binding.editor.setHeading(2)
                }

                R.drawable.ic_heading_3 -> {
                    binding.editor.setHeading(3)
                }

                R.drawable.ic_heading_4 -> {
                    binding.editor.setHeading(4)
                }

                R.drawable.ic_heading_5 -> {
                    binding.editor.setHeading(5)
                }

                R.drawable.ic_heading_6 -> {
                    binding.editor.setHeading(6)
                }

                R.drawable.ic_text_color -> {
//                    binding.editor.setTextColor()
                }

                R.drawable.ic_background_color -> {
//                    binding.editor.setTextColor()
                }

                R.drawable.ic_link -> {
                    insertLink()
                }

                R.drawable.ic_insert_image -> {
                    insertImage()
                }

                R.drawable.ic_left_align -> {
                    binding.editor.setAlignLeft()
                }

                R.drawable.ic_center_align -> {
                    binding.editor.setAlignCenter()
                }

                R.drawable.ic_right_align -> {
                    binding.editor.setAlignRight()
                }

                R.drawable.ic_un_ordered_list -> {
                    binding.editor.setBullets()
                }

                R.drawable.ic_numbered_list -> {
                    binding.editor.setNumbers()
                }

                else -> {

                }
            }
        }
    }

    //region =============================================================== TOOLBAR ===============================================================+

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.details_toolbar)
//        if (requireActivity() is NoteDetailsActivity) {
//            requireActivity().setActionBar(binding.toolbar)
//        }
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_delete_note -> {
                    viewModel.onDeleteNoteMenuItemClicked()
                }

                R.id.action_save_note -> {
                    viewModel.onSaveNoteMenuItemClicked(
                        title = binding.title.text.toString(), content = binding.editor.html
                    )
                }

                else -> {
                    it.isChecked = true
                }
            }
            true
        }
    }

    //endregion

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

    //region ================================================================ INSERT ================================================================

    private fun insertImage() {
        val insertLayoutBinding = InsertAlertDialogLayoutBinding.inflate(layoutInflater)
        insertLayoutBinding.imageTitleEditText.isVisible = false
        MaterialAlertDialogBuilder(requireContext()).setTitle("Alert Title").setMessage("This is a message for the alert dialog.")
            .setView(insertLayoutBinding.root).setPositiveButton("OK") { dialog, _ ->
                binding.editor.insertImage(insertLayoutBinding.imageLinkEditText.text.toString(), "")
                dialog.dismiss()
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun insertLink() {
        val insertLayoutBinding = InsertAlertDialogLayoutBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(requireContext()).setTitle("Alert Title").setMessage("This is a message for the alert dialog.")
            .setView(insertLayoutBinding.root).setPositiveButton("OK") { dialog, _ ->
                binding.editor.insertLink(
                    insertLayoutBinding.imageLinkEditText.text.toString(), insertLayoutBinding.imageTitleEditText.text.toString()
                )
                dialog.dismiss()
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    //endregion
}