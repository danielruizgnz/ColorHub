package com.example.pictopalette

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class ProfilePicsAdapter(private val context: Context, private val data: Array<Int>) : BaseAdapter() {

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView
        if (convertView == null) {
            // Infla el layout del ítem para cada imagen
            imageView = ImageView(context)
            imageView.layoutParams = ViewGroup.LayoutParams(200, 200)  // Ajusta el tamaño de la imagen
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imageView = convertView as ImageView
        }

        // Establece la imagen del recurso
        imageView.setImageResource(data[position])

        return imageView
    }
}
