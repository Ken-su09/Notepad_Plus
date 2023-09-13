package com.suonk.notepad_plus.ui.note.deleted_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.suonk.notepad_plus.databinding.ItemNotesListBinding

class DeletedNotesListAdapter : ListAdapter<DeletedNotesListViewState, DeletedNotesListAdapter.ViewHolder>(NotesListComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemNotesListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class ViewHolder(private val binding: ItemNotesListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(notesListViewState: DeletedNotesListViewState) {
            binding.title.text = notesListViewState.title
            binding.date.text = notesListViewState.date
            binding.content.text = notesListViewState.content
            binding.itemNoteLayout.setOnClickListener {
                notesListViewState.onClickedCallback()
            }
        }
    }

    object NotesListComparator : DiffUtil.ItemCallback<DeletedNotesListViewState>() {
        override fun areItemsTheSame(
            oldItem: DeletedNotesListViewState, newItem: DeletedNotesListViewState
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: DeletedNotesListViewState, newItem: DeletedNotesListViewState
        ): Boolean = oldItem == newItem
    }
}