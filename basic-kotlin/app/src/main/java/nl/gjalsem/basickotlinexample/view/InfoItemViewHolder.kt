package nl.gjalsem.basickotlinexample.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.android.volley.toolbox.ImageLoader
import kotlinx.android.synthetic.main.info_item.view.*
import nl.gjalsem.basickotlinexample.R
import nl.gjalsem.basickotlinexample.model.InfoItem

class InfoItemViewHolder(inflater: LayoutInflater, parent: ViewGroup, val imageLoader: ImageLoader)
    : RecyclerView.ViewHolder(inflater.inflate(R.layout.info_item, parent, false)) {

    fun bind(infoItem: InfoItem) {
        itemView.imageView.setImageUrl(infoItem.iconUrl, imageLoader)
        itemView.nameView.text = infoItem.name
        itemView.locationView.text = getLocation(infoItem)
        itemView.dateView.text = infoItem.endDate
    }

    private fun getLocation(infoItem: InfoItem): String {
        val builder = StringBuilder()
        builder.append(infoItem.city)
        if (builder.isNotEmpty()) {
            builder.append(", ");
        }
        builder.append(infoItem.state)
        return if (builder.isEmpty()) "Unknown location" else builder.toString()
    }
}
