package it.gb.sportivo.presentation.dummy_activity.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import it.gb.sportivo.R
import it.gb.sportivo.data.BoldMisterInfo
import it.gb.sportivo.data.SelectedItem


class BoldMisterAdapter(val selectMister: (BoldMisterInfo) -> Unit) :
    ListAdapter<BoldMisterInfo, BoldMisterAdapter.BoldMisterViewHolder>(BoldItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoldMisterViewHolder {
        val layout = R.layout.mister_item
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return BoldMisterViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoldMisterViewHolder, position: Int) {
        val element = getItem(position)

        element.info.image?.let { image ->
            Glide.with(holder.itemView.context)
                .load(image)
                .into(holder.image)
        }

        if (element.isSelected == SelectedItem.RightSelected) {
            holder.image2.alpha = 0.7F

            holder.image2.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context, R.color.green
                )
            )
        }else if(element.isSelected == SelectedItem.WrongSelected) {
            holder.image2.alpha = 0.7F
            holder.image2.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context, R.color.red
                )
            )
        }else if(element.isSelected == SelectedItem.WasWrongSelected) {
            holder.image2.alpha = 0.7F
            holder.image2.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.itemView.context, R.color.silver
                )
            )
        }

        holder.image.setOnClickListener {
            selectMister.invoke(element)
        }
    }

    inner class BoldMisterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<CircleImageView>(R.id.imagePlayer)
        val image2 = itemView.findViewById<CircleImageView>(R.id.imagePlayer2)
    }


}