package com.nitishsharma.aroundme.main.maps.bottomsheet

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nitishsharma.aroundme.BuildConfig
import com.nitishsharma.aroundme.api.RetrofitInstance
import com.nitishsharma.aroundme.api.models.detailedplace.DetailedPlaceResponse
import com.nitishsharma.aroundme.api.models.detailedplace.Photo
import kotlinx.coroutines.launch

class BottomSheetVM : ViewModel() {

    private val _detailedPlaceResponse = MutableLiveData<DetailedPlaceResponse>()
    val detailedPlaceResponse: LiveData<DetailedPlaceResponse>
        get() = _detailedPlaceResponse

    fun getDetailedPlace(placeId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getDetailedPlace(placeId)
                if (response.isSuccessful) {
                    _detailedPlaceResponse.postValue(response.body())
                }
            } catch (e: Exception) {
                Log.e("BottomSheetVM", e.toString())
            }
        }
    }

    fun convertPhotoReferenceToGlideLoadableLink(photos: ArrayList<Photo>): ArrayList<String> {
        return ArrayList<String>(photos.map {
            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=${it.photo_reference}&key=${BuildConfig.MAPS_API_KEY}"
        }.take(6))
    }
}