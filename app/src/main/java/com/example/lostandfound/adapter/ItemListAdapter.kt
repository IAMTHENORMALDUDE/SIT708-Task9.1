package com.example.lostandfound.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lostandfound.R
import com.example.lostandfound.model.Item

class ItemListAdapter(
    private var items: List<Item>,
    private val onItemClick: (Item) -> Unit
) : RecyclerView.Adapter<ItemListAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, onItemClick)
    }

    override fun getItemCount(): Int = items.size

    // Function to update the list of items
    fun updateItems(newItems: List<Item>) {
        items = newItems
        notifyDataSetChanged() // Consider using DiffUtil for better performance
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewInfo: TextView = itemView.findViewById(R.id.textViewItemInfo)
        private val textViewDescription: TextView = itemView.findViewById(R.id.textViewItemDescription)

        fun bind(item: Item, onItemClick: (Item) -> Unit) {
            // Combine type and name for the main info line
            textViewInfo.text = "${item.type}: ${item.name}"
            textViewDescription.text = item.description // Show description below

            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}
