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
import com.nitishsharma.aroundme.utils.Constants
import kotlinx.coroutines.launch

class BottomSheetVM : ViewModel() {

    private val _detailedPlaceResponse = MutableLiveData<DetailedPlaceResponse>()
    val detailedPlaceResponse: LiveData<DetailedPlaceResponse>
        get() = _detailedPlaceResponse

    //get details of the place according to the placeId
    fun getDetailedPlace(placeId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getDetailedPlace(placeId) // hit api
                if (response.isSuccessful) {
                    _detailedPlaceResponse.postValue(response.body()) //post data
                }
            } catch (e: Exception) {
                Log.e(Constants.ActivityNameForLogging.BOTTOM_SHEET_VM, e.toString())
            }
        }
    }

    //converting the Photo object (JSON) into array of Strings which will ultimately be loadable URIs by glide
    fun convertPhotoReferenceToGlideLoadableLink(photos: ArrayList<Photo>): ArrayList<String> {
        return ArrayList<String>(photos.map {
            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=${it.photo_reference}&key=${BuildConfig.MAPS_API_KEY}"
        }.take(6)) //take max 6 objects and do the operation
    }
}