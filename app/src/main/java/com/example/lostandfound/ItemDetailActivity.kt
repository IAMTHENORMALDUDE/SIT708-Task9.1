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
