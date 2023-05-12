package com.nitishsharma.aroundme.main.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.nitishsharma.aroundme.api.RetrofitInstance
import kotlinx.coroutines.launch

class MapsActivityVM : ViewModel() {
    private val _placesOnMap = MutableLiveData<Map<String, LatLng>>()
    val placesOnMap: LiveData<Map<String, LatLng>>
        get() = _placesOnMap

    fun fetchNearbyPlaces(currentLocation: String, placeToSearch: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getNearbyPlaces(
                    location = currentLocation,
                    type = placeToSearch.toLowerCase()
                )
                if (response.isSuccessful) {
                    val pointsOnMap: MutableMap<String, LatLng> = mutableMapOf()
                    pointsOnMap.clear()
                    response.body()?.results?.forEach {
                        pointsOnMap[it.name] =
                            LatLng(it.geometry.location.lat, it.geometry.location.lng)
                    }
                    _placesOnMap.postValue(pointsOnMap)
                    Log.i("MapsActivityVm", pointsOnMap.toString())
                }
            } catch (e: Exception) {
                Log.e("MapsActivityVM", e.toString())
            }
        }
    }
}