package com.example.pictopalette

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

        // Convertir todos los colores hexadecimales a nombres generales
        val colorNames = colors.map { hexToColorName(it) }

        // Concatenamos todos los nombres de colores (incluyendo hex si no tiene nombre)
        val query = colorNames.joinToString(", ") + ", color"

        Log.d("SimilarImagesFragment", "Consulta de colores: $query") // Para verificar la consulta

        // Realizar la consulta a la API de Unsplash con la cadena de búsqueda
        api.searchPhotos(query, clientId).enqueue(object : Callback<UnsplashResponse> {
            override fun onResponse(
                call: Call<UnsplashResponse>,
                response: Response<UnsplashResponse>
            ) {
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


    private fun hexToColorName(hex: String): String {
        return when {
            hex.startsWith("#48D070") -> "green"
            hex.startsWith("#987068") -> "brown"
            hex.startsWith("#004000") -> "dark green"
            hex.startsWith("#888888") -> "gray"
            hex.startsWith("#D0F860") -> "light green"
            else -> {
                // Si no se encuentra un color mapeado, devolvemos un color genérico
                // Por ejemplo, un color genérico para un valor hexadecimal desconocido
                if (hex.startsWith("#")) "color" else hex
            }
        }
    }
}
