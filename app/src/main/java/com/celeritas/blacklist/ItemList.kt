package com.celeritas.blacklist

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class ItemListAdapter(private val dataSet: DataSet, private val subtitleFormat: String) :
    RecyclerView.Adapter<ItemListHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemListHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ItemListHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.items.size
    }

    override fun onBindViewHolder(holder: ItemListHolder, position: Int) {
        holder.setupData(dataSet, position, subtitleFormat)

        holder.view.setOnClickListener {
            dataSet.updateBlockType(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_cell
    }

    fun removeItem(at: Int) {
        dataSet.remove(at)
        notifyDataSetChanged()
    }
}

class ItemListHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val titleLabel: TextView = view.findViewById(R.id.title)
    private val subTitleLabel: TextView = view.findViewById(R.id.subTitle)
    private val icon: ImageView = view.findViewById(R.id.icon)

    fun setupData(dataSet: DataSet, position: Int, subtitleFormat: String) {
        val item = dataSet.items[position]
        titleLabel.text = item.number
        subTitleLabel.text = subtitleFormat.format(item.date)

        val iconImage = when (item.blockType) {
            BlockType.ALL -> R.drawable.ic_perm_phone_msg_black_24dp
            BlockType.NUMBER -> R.drawable.ic_local_phone_black_24dp
            BlockType.SMS -> R.drawable.ic_sms_black_24dp
        }

        icon.setImageResource(iconImage)
    }
}

class SwipeToDelete(val adapter: ItemListAdapter) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
    override fun onMove(p0: RecyclerView, p1: RecyclerView.ViewHolder, p2: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(holder: RecyclerView.ViewHolder, direction: Int) {
        adapter.removeItem(holder.adapterPosition)
    }

}