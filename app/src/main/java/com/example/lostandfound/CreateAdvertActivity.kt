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
