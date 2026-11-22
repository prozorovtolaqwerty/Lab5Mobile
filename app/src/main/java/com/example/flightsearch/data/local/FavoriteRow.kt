package com.example.flightsearch.data.local

data class FavoriteRow(
    val id: Int,
    val departureCode: String,
    val destinationCode: String,
    val departureName: String,
    val destinationName: String,
)
