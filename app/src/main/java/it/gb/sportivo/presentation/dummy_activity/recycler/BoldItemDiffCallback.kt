package it.gb.sportivo.presentation.dummy_activity.recycler

import androidx.recyclerview.widget.DiffUtil
import it.gb.sportivo.data.BoldMisterInfo

class BoldItemDiffCallback : DiffUtil.ItemCallback<BoldMisterInfo>() {

    override fun areItemsTheSame(oldItem: BoldMisterInfo, newItem: BoldMisterInfo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BoldMisterInfo, newItem: BoldMisterInfo): Boolean {
        return oldItem == newItem
    }
}