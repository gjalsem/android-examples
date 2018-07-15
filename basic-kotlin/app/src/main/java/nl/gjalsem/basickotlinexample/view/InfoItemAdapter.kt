package nl.gjalsem.basickotlinexample.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import nl.gjalsem.basickotlinexample.model.InfoItem

class InfoItemAdapter : RecyclerView.Adapter<InfoItemViewHolder>() {
    var items: List<InfoItem> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoItemViewHolder {
        return InfoItemViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: InfoItemViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
