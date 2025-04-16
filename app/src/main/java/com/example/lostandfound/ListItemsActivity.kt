package com.example.lostandfound

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lostandfound.adapter.ItemListAdapter
import com.example.lostandfound.database.DatabaseHelper
import com.example.lostandfound.model.Item

class ListItemsActivity : AppCompatActivity() {

    private lateinit var recyclerViewItems: RecyclerView
    private lateinit var itemAdapter: ItemListAdapter
    private lateinit var dbHelper: DatabaseHelper
    private var itemList: MutableList<Item> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_items)

        dbHelper = DatabaseHelper(this)
        recyclerViewItems = findViewById(R.id.recyclerViewItems)
        recyclerViewItems.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with empty list first
        itemAdapter = ItemListAdapter(itemList) { item ->
            // Handle item click: navigate to ItemDetailActivity
            val intent = Intent(this, ItemDetailActivity::class.java)
            intent.putExtra(ItemDetailActivity.EXTRA_ITEM_ID, item.id)
            startActivity(intent)
        }
        recyclerViewItems.adapter = itemAdapter

        // Load items in onResume to refresh list when returning
    }

    override fun onResume() {
        super.onResume()
        loadItems()
    }

    private fun loadItems() {
        itemList.clear()
        itemList.addAll(dbHelper.getAllItems())
        itemAdapter.updateItems(itemList) // Update adapter data
    }
}
