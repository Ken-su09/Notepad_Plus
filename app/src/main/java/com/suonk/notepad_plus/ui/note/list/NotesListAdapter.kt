package com.suonk.notepad_plus.ui.note.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.suonk.notepad_plus.databinding.ItemNotesListBinding

class NotesListAdapter : ListAdapter<NotesListViewState, NotesListAdapter.ViewHolder>(NotesListComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemNotesListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class ViewHolder(private val binding: ItemNotesListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(notesListViewState: NotesListViewState) {
            binding.title.text = notesListViewState.title
            binding.date.text = notesListViewState.date
            binding.content.text = notesListViewState.content
            binding.itemNoteLayout.setOnClickListener {
                notesListViewState.onClickedCallback()
            }
        }
    }

    object NotesListComparator : DiffUtil.ItemCallback<NotesListViewState>() {
        override fun areItemsTheSame(
            oldItem: NotesListViewState, newItem: NotesListViewState
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: NotesListViewState, newItem: NotesListViewState
        ): Boolean = oldItem == newItem
    }
}