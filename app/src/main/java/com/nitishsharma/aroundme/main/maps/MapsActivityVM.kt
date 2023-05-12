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
//    private val _places = MutableLiveData<ArrayList<Place>>()
//    val places: LiveData<ArrayList<Place>>
//        get() = _places

    private val _placesOnMap = MutableLiveData<Map<String, LatLng>>()
    val placesOnMap: LiveData<Map<String, LatLng>>
        get() = _placesOnMap

    fun fetchNearbyPlaces(currentLocation: String, placeToSearch: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getNearbyHospitals(
                    location = currentLocation,
                    type = placeToSearch
                )
                if (response.isSuccessful) {
                    val pointsOnMap: MutableMap<String, LatLng> = mutableMapOf()
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