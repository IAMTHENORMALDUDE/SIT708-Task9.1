// File: app/src/main/java/com/example/lostandfound/MainActivity.kt
package com.example.lostandfound

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var btnCreateAdvert: Button
    private lateinit var btnShowItems: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Apply window insets padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnCreateAdvert = findViewById(R.id.btnCreateAdvert)
        btnShowItems = findViewById(R.id.btnShowItems)

        btnCreateAdvert.setOnClickListener {
            val intent = Intent(this, CreateAdvertActivity::class.java)
            startActivity(intent)
        }

        btnShowItems.setOnClickListener {
            val intent = Intent(this, ListItemsActivity::class.java)
            startActivity(intent)
        }
    }
}

// File: app/src/main/java/com/example/lostandfound/CreateAdvertActivity.kt
package com.example.lostandfound

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lostandfound.database.DatabaseHelper
import com.example.lostandfound.model.Item

class CreateAdvertActivity : AppCompatActivity() {

    private lateinit var radioGroupType: RadioGroup
    private lateinit var radioLost: RadioButton
    private lateinit var radioFound: RadioButton
    private lateinit var editTextName: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextDate: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var btnSave: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_advert)

        dbHelper = DatabaseHelper(this)

        radioGroupType = findViewById(R.id.radioGroupType)
        radioLost = findViewById(R.id.radioLost)
        radioFound = findViewById(R.id.radioFound)
        editTextName = findViewById(R.id.editTextName)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextDate = findViewById(R.id.editTextDate)
        editTextLocation = findViewById(R.id.editTextLocation)
        btnSave = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {
            saveItem()
        }
    }

    private fun saveItem() {
        val selectedTypeId = radioGroupType.checkedRadioButtonId
        if (selectedTypeId == -1) {
            Toast.makeText(this, "Please select post type", Toast.LENGTH_SHORT).show()
            return
        }
        val type = if (selectedTypeId == R.id.radioLost) "Lost" else "Found"

        val name = editTextName.text.toString().trim()
        val phone = editTextPhone.text.toString().trim()
        val description = editTextDescription.text.toString().trim()
        val date = editTextDate.text.toString().trim()
        val location = editTextLocation.text.toString().trim()

        if (name.isEmpty() || phone.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val item = Item(type = type, name = name, phone = phone, description = description, date = date, location = location)
        val id = dbHelper.insertItem(item)

        if (id > -1) {
            Toast.makeText(this, "Advert saved successfully", Toast.LENGTH_SHORT).show()
            finish() // Close the activity after saving
        } else {
            Toast.makeText(this, "Error saving advert", Toast.LENGTH_SHORT).show()
        }
    }
}

// File: app/src/main/java/com/example/lostandfound/ListItemsActivity.kt
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

// File: app/src/main/java/com/example/lostandfound/ItemDetailActivity.kt
package com.example.lostandfound

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lostandfound.database.DatabaseHelper
import com.example.lostandfound.model.Item

class ItemDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ITEM_ID = "extra_item_id"
    }

    private lateinit var textViewName: TextView
    private lateinit var textViewDate: TextView
    private lateinit var textViewLocation: TextView
    private lateinit var textViewDescription: TextView
    private lateinit var textViewContact: TextView
    private lateinit var btnRemove: Button
    private lateinit var dbHelper: DatabaseHelper
    private var currentItemId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        dbHelper = DatabaseHelper(this)

        textViewName = findViewById(R.id.textViewItemDetailName)
        textViewDate = findViewById(R.id.textViewItemDetailDate)
        textViewLocation = findViewById(R.id.textViewItemDetailLocation)
        textViewDescription = findViewById(R.id.textViewItemDetailDescription)
        textViewContact = findViewById(R.id.textViewItemDetailContact)
        btnRemove = findViewById(R.id.btnRemoveItem)

        currentItemId = intent.getIntExtra(EXTRA_ITEM_ID, -1)

        if (currentItemId != -1) {
            loadItemDetails(currentItemId)
        } else {
            Toast.makeText(this, "Error: Item ID not found", Toast.LENGTH_SHORT).show()
            finish() // Close activity if ID is invalid
        }

        btnRemove.setOnClickListener {
            removeItem(currentItemId)
        }
    }

    private fun loadItemDetails(itemId: Int) {
        val item = dbHelper.getItemById(itemId)
        if (item != null) {
            textViewName.text = "${item.type}: ${item.name}" // Include type
            textViewDate.text = "Date: ${item.date}"
            textViewLocation.text = "Location: ${item.location}"
            textViewDescription.text = "Description: ${item.description}"
            textViewContact.text = "Contact: ${item.phone}"
        } else {
            Toast.makeText(this, "Error loading item details", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun removeItem(itemId: Int) {
        val result = dbHelper.deleteItem(itemId)
        if (result > 0) {
            Toast.makeText(this, "Item removed successfully", Toast.LENGTH_SHORT).show()
            finish() // Close activity after removal
        } else {
            Toast.makeText(this, "Error removing item", Toast.LENGTH_SHORT).show()
        }
    }
}

// File: app/src/main/java/com/example/lostandfound/adapter/ItemListAdapter.kt
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

// File: app/src/main/java/com/example/lostandfound/database/DatabaseHelper.kt
package com.example.lostandfound.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.lostandfound.model.Item

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "lostandfound.db"
        private const val DATABASE_VERSION = 1

        // Table name
        private const val TABLE_ITEMS = "items"

        // Column names
        private const val COLUMN_ID = "id"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PHONE = "phone"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_LOCATION = "location"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_ITEMS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TYPE TEXT NOT NULL,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_LOCATION TEXT NOT NULL
            )
        """.trimIndent()
        
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ITEMS")
        onCreate(db)
    }

    // Insert a new item
    fun insertItem(item: Item): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TYPE, item.type)
            put(COLUMN_NAME, item.name)
            put(COLUMN_PHONE, item.phone)
            put(COLUMN_DESCRIPTION, item.description)
            put(COLUMN_DATE, item.date)
            put(COLUMN_LOCATION, item.location)
        }
        
        val id = db.insert(TABLE_ITEMS, null, values)
        db.close()
        return id
    }

    // Get all items
    fun getAllItems(): List<Item> {
        val itemList = mutableListOf<Item>()
        val selectQuery = "SELECT * FROM $TABLE_ITEMS"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(selectQuery, null)
        
        if (cursor.moveToFirst()) {
            do {
                val item = Item(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION))
                )
                itemList.add(item)
            } while (cursor.moveToNext())
        }
        
        cursor.close()
        db.close()
        return itemList
    }

    // Delete an item by ID
    fun deleteItem(itemId: Int): Int {
        val db = this.writableDatabase
        val result = db.delete(TABLE_ITEMS, "$COLUMN_ID = ?", arrayOf(itemId.toString()))
        db.close()
        return result
    }

    // Get item by ID
    fun getItemById(itemId: Int): Item? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_ITEMS,
            null,
            "$COLUMN_ID = ?",
            arrayOf(itemId.toString()),
            null,
            null,
            null
        )

        var item: Item? = null
        if (cursor.moveToFirst()) {
            item = Item(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHONE)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION))
            )
        }
        
        cursor.close()
        db.close()
        return item
    }
}

// File: app/src/main/java/com/example/lostandfound/model/Item.kt
package com.example.lostandfound.model

data class Item(
    val id: Int = 0, // Default value for auto-increment ID
    val type: String,
    val name: String,
    val phone: String,
    val description: String,
    val date: String,
    val location: String
)
