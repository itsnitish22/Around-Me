package com.nitishsharma.aroundme.main.maps.bottomsheet.slider

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nitishsharma.aroundme.databinding.ItemSliderBinding

class PhotosAdapter(
    private val slideList: ArrayList<String>
) : RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val binding = ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        val placeImage = slideList[position]
        Glide.with(holder.binding.root.context).load(placeImage)
            .into(holder.binding.placeIv)
    }

    override fun getItemCount(): Int {
        return if (slideList.size >= 6) 6 else slideList.size
    }

    inner class PhotosViewHolder(val binding: ItemSliderBinding) :
        RecyclerView.ViewHolder(binding.root)
}