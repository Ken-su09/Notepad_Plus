package com.suonk.notepad_plus.ui.note.details.actions_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.suonk.notepad_plus.databinding.ItemActionsListBinding

class ActionsListAdapter : ListAdapter<EditTextAction, ActionsListAdapter.ViewHolder>(ActionsComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemActionsListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class ViewHolder(private val binding: ItemActionsListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(action: EditTextAction) {
            binding.actionIcon.setImageResource(action.icon)
            binding.actionIcon.setOnClickListener {
                action.onClickedCallback()
            }
            binding.root.background = action.background
        }
    }

    object ActionsComparator : DiffUtil.ItemCallback<EditTextAction>() {
        override fun areItemsTheSame(oldItem: EditTextAction, newItem: EditTextAction): Boolean = oldItem.icon == newItem.icon
        override fun areContentsTheSame(oldItem: EditTextAction, newItem: EditTextAction): Boolean = oldItem == newItem
    }
}