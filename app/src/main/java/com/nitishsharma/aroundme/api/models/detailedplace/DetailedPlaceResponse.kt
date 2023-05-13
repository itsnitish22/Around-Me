package com.nitishsharma.aroundme.api.models.detailedplace

data class DetailedPlaceResponse(
    val result: DetailedPlace?
)

data class DetailedPlace(
    val formatted_address: String?,
    val international_phone_number: String?,
    val name: String?,
    val opening_hours: OpeningHours?,
    val photos: ArrayList<Photo>?,
    val rating: Float?,
    val reviews: ArrayList<Review?>?,
    val types: ArrayList<String>?
)

data class Photo(
    val photo_reference: String?
)

data class Review(
    val author_name: String?,
    val profile_photo_url: String?,
    val rating: Float?,
    val text: String?
)

data class OpeningHours(
    val open_now: Boolean?
)