package com.nitishsharma.aroundme.main.maps

import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nitishsharma.aroundme.api.RetrofitInstance
import kotlinx.coroutines.launch
import java.io.IOException

class MapsActivityVM : ViewModel() {

    private val _placesOnMap = MutableLiveData<MutableList<Pair<String, Pair<String, LatLng>>>>()
    val placesOnMap: LiveData<MutableList<Pair<String, Pair<String, LatLng>>>>
        get() = _placesOnMap

    private val _searchedLocation = MutableLiveData<LatLng?>()
    val searchedLocation: LiveData<LatLng?>
        get() = _searchedLocation

    fun fetchNearbyPlaces(currentLocation: String, placeToSearch: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getNearbyPlaces(
                    location = currentLocation,
                    type = placeToSearch.toLowerCase()
                )
                if (response.isSuccessful) {
                    val pointsOnMap: MutableList<Pair<String, Pair<String, LatLng>>> =
                        mutableListOf()
                    response.body()?.results?.forEach {
                        pointsOnMap.add(
                            Pair(
                                it.place_id,
                                Pair(
                                    it.name,
                                    LatLng(it.geometry.location.lat, it.geometry.location.lng)
                                )
                            )
                        )
                    }
                    _placesOnMap.postValue(pointsOnMap)
                }
            } catch (e: Exception) {
                Log.e("MapsActivityVM", e.toString())
            }
        }
    }

    fun searchPlace(toSearch: String, geocoder: Geocoder) {
        viewModelScope.launch {
            try {
                val addresses = geocoder.getFromLocationName(toSearch, 1)
                val location =
                    addresses?.get(0)?.let { LatLng(it.latitude, addresses[0].longitude) }
                _searchedLocation.postValue(location)
            } catch (e: IOException) {
                Log.e("MapsActivityVM", e.toString())
            }
        }
    }
}