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
    private lateinit var btnShowOnMap: Button

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
        btnShowOnMap = findViewById(R.id.btnShowOnMap)

        btnCreateAdvert.setOnClickListener {
            val intent = Intent(this, CreateAdvertActivity::class.java)
            startActivity(intent)
        }

        btnShowItems.setOnClickListener {
            val intent = Intent(this, ListItemsActivity::class.java)
            startActivity(intent)
        }
        
        btnShowOnMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }
}
