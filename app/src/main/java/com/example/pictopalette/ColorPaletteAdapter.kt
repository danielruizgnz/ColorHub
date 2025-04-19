package com.example.pictopalette

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pictopalette.databinding.ItemColorPaletteBinding

class ColorPaletteAdapter(private val colorPalettes: List<ColorPalette>) :
    RecyclerView.Adapter<ColorPaletteAdapter.ColorPaletteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorPaletteViewHolder {
        val binding = ItemColorPaletteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ColorPaletteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorPaletteViewHolder, position: Int) {
        val palette = colorPalettes[position]
        holder.bind(palette)
    }

    override fun getItemCount(): Int = colorPalettes.size

    class ColorPaletteViewHolder(private val binding: ItemColorPaletteBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(palette: ColorPalette) {
            binding.tvPaletteName.text = palette.name
            // Aquí puedes actualizar la vista con los colores
            updateColorViews(palette.colors)
        }

        private fun updateColorViews(colors: List<Int>) {
            val colorViews = listOf(binding.color1, binding.color2, binding.color3, binding.color4, binding.color5)
            colors.forEachIndexed { index, color ->
                if (index < colorViews.size) {
                    colorViews[index].setBackgroundColor(color)
                }
            }
        }
    }
}
