package com.example.pictopalette

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pictopalette.databinding.ItemImageBinding
import com.squareup.picasso.Picasso

class UnsplashAdapter(private val images: List<UnsplashPhoto>) :
    RecyclerView.Adapter<UnsplashAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int = images.size

    inner class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(image: UnsplashPhoto) {
            Picasso.get().load(image.urls.small).into(binding.imageView)  // Usamos Picasso para cargar la imagen
        }
    }
}
