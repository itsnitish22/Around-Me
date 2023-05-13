package com.nitishsharma.aroundme.api.models.place

data class PlaceResponse(
    val results: ArrayList<Place>
)

data class Place(
    val geometry: Geometry,
    val icon: String,
    val name: String,
    val place_id: String
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)