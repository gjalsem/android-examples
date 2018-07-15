package nl.gjalsem.basickotlinexample.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.android.volley.toolbox.ImageLoader
import kotlinx.android.synthetic.main.info_item.view.*
import nl.gjalsem.basickotlinexample.R
import nl.gjalsem.basickotlinexample.model.InfoItem

/**
 * Binds an InfoItem object to the info_item layout.
 */
class InfoItemViewHolder(parent: ViewGroup, private val imageLoader: ImageLoader)
    : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.info_item, parent, false)) {

    private val context = parent.context

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
            builder.append(", ")
        }
        builder.append(infoItem.state)

        return if (builder.isEmpty())
            context.getString(R.string.unknown_location)
        else
            builder.toString()
    }
}
