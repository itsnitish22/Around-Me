package com.nitishsharma.aroundme.main.maps.bottomsheet.slider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nitishsharma.aroundme.R

class PhotosAdapter(
    slideList: ArrayList<String>
) : RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>() {

    var sliderList: ArrayList<String> = slideList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slider, parent, false)
        return PhotosViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        val placeImage = sliderList[position]
        Glide.with(holder.imageView.context).load(placeImage).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return if (sliderList.size >= 3) 3 else sliderList.size
    }

    class PhotosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.placeIv)
    }
}