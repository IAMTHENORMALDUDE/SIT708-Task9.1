package com.example.lostandfound

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lostandfound.database.DatabaseHelper
import com.example.lostandfound.model.Item
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class CreateAdvertActivity : AppCompatActivity() {

    private lateinit var radioGroupType: RadioGroup
    private lateinit var radioLost: RadioButton
    private lateinit var radioFound: RadioButton
    private lateinit var editTextName: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextDate: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var btnGetCurrentLocation: Button
    private lateinit var btnSave: Button
    private lateinit var dbHelper: DatabaseHelper
    
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient
    
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_advert)

        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }
        placesClient = Places.createClient(this)
        
        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        dbHelper = DatabaseHelper(this)

        radioGroupType = findViewById(R.id.radioGroupType)
        radioLost = findViewById(R.id.radioLost)
        radioFound = findViewById(R.id.radioFound)
        editTextName = findViewById(R.id.editTextName)
        editTextPhone = findViewById(R.id.editTextPhone)
        editTextDescription = findViewById(R.id.editTextDescription)
        editTextDate = findViewById(R.id.editTextDate)
        editTextLocation = findViewById(R.id.editTextLocation)
        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation)
        btnSave = findViewById(R.id.btnSave)

        // Set up location autocomplete when clicking on the location field
        editTextLocation.setOnClickListener {
            // You can implement a custom autocomplete UI here or use the Places SDK Autocomplete widget
            // For simplicity, we'll just show a toast message
            Toast.makeText(this, "Enter a location or use Get Current Location", Toast.LENGTH_SHORT).show()
        }
        
        btnGetCurrentLocation.setOnClickListener {
            getCurrentLocation()
        }

        btnSave.setOnClickListener {
            saveItem()
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                currentLatitude = it.latitude
                currentLongitude = it.longitude
                
                // Update the location EditText with the coordinates
                val locationText = "${it.latitude}, ${it.longitude}"
                editTextLocation.setText(locationText)
                
                Toast.makeText(this, "Current location set", Toast.LENGTH_SHORT).show()
            } ?: run {
                Toast.makeText(this, "Could not get location. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
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

        val item = Item(
            type = type, 
            name = name, 
            phone = phone, 
            description = description, 
            date = date, 
            location = location,
            latitude = currentLatitude,
            longitude = currentLongitude
        )
        val id = dbHelper.insertItem(item)

        if (id > -1) {
            Toast.makeText(this, "Advert saved successfully", Toast.LENGTH_SHORT).show()
            finish() // Close the activity after saving
        } else {
            Toast.makeText(this, "Error saving advert", Toast.LENGTH_SHORT).show()
        }
    }
}
