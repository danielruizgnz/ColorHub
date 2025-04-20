package com.example.pictopalette

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pictopalette.databinding.FragmentSimilarImagesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SimilarImagesFragment : Fragment() {

    private lateinit var binding: FragmentSimilarImagesBinding
    private val clientId = "WsRYLW2BwM6HJkgsGJq4GjD8Om_AF7s_l54uD8JHoLQ" // Tu API key de Unsplash

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSimilarImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val colors = arguments?.getStringArrayList("colors")
        if (colors.isNullOrEmpty()) {
            Log.e("SimilarImagesFragment", "No se pasaron colores al fragment.")
            return
        }

        Log.d("SimilarImagesFragment", "Colores recibidos: $colors")
        searchImages(colors)
    }

    private fun searchImages(colors: List<String>) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(UnsplashApi::class.java)

        val colorNames = colors.map { getApproximateColorName(it) }

        val randomPage = (1..5).random()
        val query = colorNames.joinToString(", ") + ", aesthetic"

        Log.d("SimilarImagesFragment", "Consulta de colores: $query")

        api.searchPhotos(query, clientId, page = randomPage, perPage = 30).enqueue(object : Callback<UnsplashResponse> {
            override fun onResponse(call: Call<UnsplashResponse>, response: Response<UnsplashResponse>) {
                val images = response.body()?.results ?: return
                binding.recyclerView.apply {
                    layoutManager = GridLayoutManager(requireContext(), 2)
                    adapter = UnsplashAdapter(images)
                }
            }

            override fun onFailure(call: Call<UnsplashResponse>, t: Throwable) {
                Log.e("SimilarImagesFragment", "Error al cargar las imágenes: ${t.message}")
            }
        })
    }

    private fun getApproximateColorName(hex: String): String {
        val hexColor = Color.parseColor(hex)
        val r1 = Color.red(hexColor)
        val g1 = Color.green(hexColor)
        val b1 = Color.blue(hexColor)

        val colorNames = mapOf(
            "black" to Color.rgb(0, 0, 0),
            "white" to Color.rgb(255, 255, 255),
            "red" to Color.rgb(255, 0, 0),
            "green" to Color.rgb(0, 128, 0),
            "blue" to Color.rgb(0, 0, 255),
            "yellow" to Color.rgb(255, 255, 0),
            "orange" to Color.rgb(255, 165, 0),
            "purple" to Color.rgb(128, 0, 128),
            "brown" to Color.rgb(165, 42, 42),
            "gray" to Color.rgb(128, 128, 128),
            "pink" to Color.rgb(255, 192, 203),
            "light green" to Color.rgb(144, 238, 144)
        )

        return colorNames.minByOrNull { (_, rgb) ->
            val r2 = Color.red(rgb)
            val g2 = Color.green(rgb)
            val b2 = Color.blue(rgb)
            (r1 - r2).let { it * it } + (g1 - g2).let { it * it } + (b1 - b2).let { it * it }
        }?.key ?: "color"
    }
}
