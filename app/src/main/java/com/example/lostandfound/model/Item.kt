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
