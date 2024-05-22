package com.example.shoppinglist

import com.google.gson.annotations.SerializedName

data class LocationData (
    val latitude: Double,
    val longitude: Double
)

data class GeocodingResponse(
    val results: List<GeocodingResult>,
    val status: String
)

data class GeocodingResult(
    @SerializedName("formatted_address")
    val formattedAddress: String
)