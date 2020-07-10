package com.example.foodhunt.model

data class OrderHistory(
    val orderId: String,
    val restaurantName: String,
    val totalCost: String,
    val orderPlacesAt: String
)