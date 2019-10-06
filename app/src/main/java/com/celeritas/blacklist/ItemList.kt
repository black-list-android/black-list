package com.celeritas.blacklist

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_cell.view.*

class ItemListAdapter(private val dataSet: DataSet, private val subtitleFormat: String, private val canEdit: Boolean) :
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

        if (canEdit) {
            holder.view.setOnClickListener {
                showHintDialog(holder.view.context, position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_cell
    }

    fun removeItem(at: Int) {
        dataSet.remove(at)
        notifyDataSetChanged()
    }

    private fun showHintDialog(context: Context, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Set hint")

        val layoutInflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = layoutInflater.inflate(R.layout.hint_input, null)

        val categoryEditText = view.findViewById(R.id.categoryEditText) as EditText

        builder.setView(view)

        // set up the ok button
        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            val hint = categoryEditText.text ?: ""

            // do something
            dataSet.updateHint(position, hint.toString())
            notifyDataSetChanged()

            dialog.dismiss()
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }
}

class ItemListHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private val titleLabel: TextView = view.findViewById(R.id.title)
    private val subTitleLabel: TextView = view.findViewById(R.id.subTitle)
    private val hintLabel: TextView = view.findViewById(R.id.hint)
    private val icon: ImageView = view.findViewById(R.id.icon)

    fun setupData(dataSet: DataSet, position: Int, subtitleFormat: String) {
        val item = dataSet.items[position]
        titleLabel.text = item.number
        subTitleLabel.text = subtitleFormat.format(item.date)
        hintLabel.text = item.hint


        icon.setImageResource(R.drawable.ic_local_phone_black_24dp)
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