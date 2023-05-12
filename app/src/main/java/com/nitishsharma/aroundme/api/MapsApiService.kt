package com.nitishsharma.aroundme.api

import com.nitishsharma.aroundme.BuildConfig
import com.nitishsharma.aroundme.api.models.PlaceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MapsApiService {
    @GET("nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int = 10000,
        @Query("type") type: String,
        @Query("key") key: String = BuildConfig.MAPS_API_KEY
    ): Response<PlaceResponse>
}