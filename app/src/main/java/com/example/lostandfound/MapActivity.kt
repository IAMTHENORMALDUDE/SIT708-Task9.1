package com.example.lostandfound

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lostandfound.database.DatabaseHelper
import com.example.lostandfound.model.Item
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var dbHelper: DatabaseHelper
    private var items: List<Item> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Initialize database helper
        dbHelper = DatabaseHelper(this)
        
        // Get all items from database
        items = dbHelper.getAllItems()

        // Initialize map fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check if we have any items to display
        if (items.isEmpty()) {
            Toast.makeText(this, "No items to display on map", Toast.LENGTH_SHORT).show()
            return
        }

        // Add markers for all items with valid coordinates
        val builder = LatLngBounds.Builder()
        var hasValidMarkers = false

        for (item in items) {
            // Skip items with invalid coordinates (0,0 or null)
            if (item.latitude == 0.0 && item.longitude == 0.0) {
                continue
            }

            val position = LatLng(item.latitude, item.longitude)
            val markerColor = if (item.type == "Lost") {
                BitmapDescriptorFactory.HUE_RED
            } else {
                BitmapDescriptorFactory.HUE_GREEN
            }

            mMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title("${item.type}: ${item.name}")
                    .snippet(item.description)
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
            )

            builder.include(position)
            hasValidMarkers = true
        }

        // If we have valid markers, move camera to show all markers
        if (hasValidMarkers) {
            val bounds = builder.build()
            val padding = 500 // offset from edges of the map in pixels
            val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
            mMap.animateCamera(cameraUpdate)
        } else {
            // If no valid markers, show a default location (e.g., Australia)
            val defaultLocation = LatLng(-25.0, 135.0) // Center of Australia
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 4f))
            Toast.makeText(this, "No items with valid locations to display", Toast.LENGTH_SHORT).show()
        }
    }
}
