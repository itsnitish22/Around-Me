package com.nitishsharma.aroundme.api.models

data class PlaceResponse(
    val results: ArrayList<Place>
)

data class Place(
    val geometry: Geometry,
    val name: String
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)