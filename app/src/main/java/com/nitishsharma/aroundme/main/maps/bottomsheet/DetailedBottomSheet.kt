package com.nitishsharma.aroundme.main.maps.bottomsheet

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nitishsharma.aroundme.R
import com.nitishsharma.aroundme.api.models.detailedplace.DetailedPlace
import com.nitishsharma.aroundme.databinding.DetailedBottomsheetBinding
import com.nitishsharma.aroundme.main.maps.bottomsheet.slider.PhotosAdapter

class DetailedBottomSheet : BottomSheetDialogFragment() {
    private val bottomSheetVM: BottomSheetVM by viewModels()
    private lateinit var binding: DetailedBottomsheetBinding
    private lateinit var placeId: String

    companion object {
        fun newInstance(placeId: String): DetailedBottomSheet {
            val fragment = DetailedBottomSheet()
            val args = Bundle().apply {
                putString("ARG_PLACE_ID", placeId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = DetailedBottomsheetBinding.inflate(inflater, container, false).also {
        binding = it
        placeId =
            arguments?.getString("ARG_PLACE_ID")!!
    }.root

    private fun initSliderAdapter(photos: ArrayList<String>) {
        binding.recyclerView.adapter = PhotosAdapter(photos)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomSheetVM.getDetailedPlace(placeId)
        binding.progressBar.visibility = View.VISIBLE
        initObservers()
    }

    private fun initObservers() {
        bottomSheetVM.detailedPlaceResponse.observe(requireActivity(), Observer {
            Log.i("DetailedBottomSheet", it.result.toString())
            it.result?.photos?.let { photoRef ->
                bottomSheetVM.convertPhotoReferenceToGlideLoadableLink(
                    photoRef
                )
            }?.let { formattedArrayOfPhotos ->
                initSliderAdapter(formattedArrayOfPhotos)
                Log.i("DetailedBotttomSheet", formattedArrayOfPhotos.toString())
            }
            it.result?.let { results -> initViews(results) }
        })
    }

//    private fun initComposeView(formattedArrayOfPhotos: ArrayList<String>) {
//        binding.composeView.setContent {
//            LazyRow() {
//                items(items = formattedArrayOfPhotos) {
//                    PlaceImage(it)
//                }
//            }
//        }
//    }

    @SuppressLint("SetTextI18n")
    private fun initViews(place: DetailedPlace) {
        binding.apply {
            place.name?.let {
                nameTv.text = it
            }
            place.opening_hours?.open_now?.let {
                if (!it) {
                    openNowTv.apply {
                        setTextColor(ContextCompat.getColor(context, R.color.red))
                        text = "Closed"
                    }
                } else {
                    openNowTv.apply {
                        setTextColor(ContextCompat.getColor(context, R.color.green))
                        text = "Open"
                    }
                }
            }
            place.rating?.let {
                ratingTv.text = "Ratings: $it"
            }
            place.international_phone_number?.let {
                phoneTv.text = it
            }
            place.formatted_address?.let {
                addressTv.text = it
            }
        }
        binding.progressBar.visibility = View.GONE
    }
}