package nl.gjalsem.basickotlinexample.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.info_item.view.*
import nl.gjalsem.basickotlinexample.R
import nl.gjalsem.basickotlinexample.model.InfoItem

class InfoItemViewHolder(inflater: LayoutInflater, parent: ViewGroup)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.info_item, parent, false)) {
    fun bind(infoItem: InfoItem) {
        itemView.nameView.text = infoItem.name
    }
}
