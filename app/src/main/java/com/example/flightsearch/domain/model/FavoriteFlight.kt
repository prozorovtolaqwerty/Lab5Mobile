package com.example.flightsearch.domain.model

data class FavoriteFlight(
    val id: Int,
    val departureCode: String,
    val destinationCode: String,
    val departureName: String,
    val destinationName: String,
)
