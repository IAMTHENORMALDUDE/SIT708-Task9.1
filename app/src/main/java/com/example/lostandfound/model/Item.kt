package com.example.lostandfound.model

data class Item(
    val id: Int = 0,
    val type: String,
    val name: String,
    val phone: String,
    val description: String,
    val date: String,
    val location: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
