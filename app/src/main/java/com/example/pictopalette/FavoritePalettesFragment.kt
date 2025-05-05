package com.example.pictopalette

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pictopalette.databinding.FragmentFavoritePalettesBinding
import com.example.pictopalette.databinding.ItemPaletteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoritePalettesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritePalettesBinding
    private val db = FirebaseFirestore.getInstance()
    private val firebaseUser get() = FirebaseAuth.getInstance().currentUser
    private lateinit var paletteAdapter: PaletteAdapter

    // Mapeo de "estados de ánimo de color" a URLs de playlists de Spotify
    // *** DEBES DEFINIR TUS PROPIOS ESTADOS DE ÁNIMO Y URLs DE PLAYLISTS AQUÍ ***
    private val moodPlaylistMap = mapOf(
        "Energético" to "https://open.spotify.com/playlist/37i9dQZF1DXcBWIGoYBM5M", // URL de tu playlist "Energético"
        "Tranquilo" to "https://open.spotify.com/playlist/37i9dQZF1DXcBWIGoYBM5M",    // URL de tu playlist "Tranquilo"
        "Misterioso" to "https://open.spotify.com/playlist/37i9dQZF1DXcBWIGoYBM5M",  // URL de tu playlist "Misterioso"
        "Feliz" to "https://open.spotify.com/playlist/37i9dQZF1DXcBWIGoYBM5M",      // URL de tu playlist "Feliz"
        "Melancólico" to "https://open.spotify.com/playlist/37i9dQZF1DXcBWIGoYBM5M", // URL de tu playlist "Melancólico"
        // Añade más estados de ánimo y sus URLs correspondientes
    )

    // Puedes tener una URL de playlist por defecto si no se encuentra un mapeo específico
    private val DEFAULT_PLAYLIST_URL = "https://open.spotify.com/playlist/37i9dQZF1DXcBWIGoYBM5M" // URL de tu playlist por defecto


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritePalettesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configura el RecyclerView y el Adapter
        paletteAdapter = PaletteAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = paletteAdapter
        }

        // Carga las paletas favoritas del usuario
        cargarPaletas()
    }

    // Función para cargar las paletas desde Firestore
    private fun cargarPaletas() {
        firebaseUser?.let { user ->
            db.collection("users")
                .document(user.uid)
                .collection("palettes")
                .get()
                .addOnSuccessListener { documents ->
                    val palettes = documents.mapNotNull { doc ->
                        val palette = doc.toObject(PaletteData::class.java)
                        palette.id = doc.id // Asigna el ID del documento a la paleta
                        palette
                    }
                    paletteAdapter.submitList(palettes) // Actualiza la lista en el adapter
                }
                .addOnFailureListener { e ->
                    Log.e("FavoritePalettes", "Error al cargar paletas", e)
                    mostrarMensaje("Error al cargar paletas.")
                }
        }
    }

    // Data class para representar una paleta (debe coincidir con la estructura en Firestore)
    data class PaletteData(
        var id: String = "", // Añadimos id para almacenar el ID del documento Firestore
        val title: String = "",
        val colors: List<String> = emptyList(),
        val timestamp: Long = 0
    )

    // Adapter para el RecyclerView
    inner class PaletteAdapter : RecyclerView.Adapter<PaletteAdapter.PaletteViewHolder>() {
        private val palettes = mutableListOf<PaletteData>()

        // Función para actualizar la lista de paletas en el adapter
        fun submitList(list: List<PaletteData>) {
            palettes.clear()
            palettes.addAll(list)
            notifyDataSetChanged() // Notifica al RecyclerView que los datos han cambiado
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaletteViewHolder {
            val itemBinding =
                ItemPaletteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PaletteViewHolder(itemBinding)
        }

        override fun onBindViewHolder(holder: PaletteViewHolder, position: Int) {
            holder.bind(palettes[position])
        }

        override fun getItemCount(): Int = palettes.size

        // ViewHolder para cada item de la paleta en el RecyclerView
        inner class PaletteViewHolder(private val binding: ItemPaletteBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(palette: PaletteData) {
                binding.paletteTitleTextView.text = palette.title
                binding.paletteColorsLayout.removeAllViews() // Limpia vistas de colores anteriores

                // Añade vistas de color para cada color en la paleta
                palette.colors.forEach { hexColor ->
                    try {
                        val colorView = View(requireContext()).apply {
                            layoutParams = LinearLayout.LayoutParams(0, 100, 1f) // Peso 1f para distribuir el espacio
                            setBackgroundColor(Color.parseColor(hexColor)) // Establece el color de fondo
                        }
                        binding.paletteColorsLayout.addView(colorView)
                    } catch (e: IllegalArgumentException) {
                        Log.e("PaletteAdapter", "Color hexadecimal inválido: $hexColor", e)
                        // Opcional: mostrar un indicador de error o usar un color por defecto
                    }
                }

                // Listeners para los botones
                binding.shareButton.setOnClickListener {
                    val text = palette.colors.joinToString(", ") { it }
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Paleta '${palette.title}': $text")
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(intent, "Compartir paleta"))
                }

                binding.editButton.setOnClickListener {
                    val editText = EditText(requireContext()).apply {
                        setText(palette.title)
                        hint = "Nuevo título"
                    }

                    AlertDialog.Builder(requireContext())
                        .setTitle("Editar título de la paleta")
                        .setView(editText)
                        .setPositiveButton("Guardar") { _, _ ->
                            val newTitle = editText.text.toString().trim()
                            if (newTitle.isNotEmpty()) {
                                // Actualiza en Firestore
                                firebaseUser?.let { user ->
                                    db.collection("users")
                                        .document(user.uid)
                                        .collection("palettes")
                                        .document(palette.id)
                                        .update("title", newTitle)
                                        .addOnSuccessListener {
                                            // Actualiza en la lista local y notifica el cambio
                                            val pos = palettes.indexOfFirst { it.id == palette.id }
                                            if (pos != -1) {
                                                palettes[pos] = palette.copy(title = newTitle) // Crea una copia actualizada
                                                notifyItemChanged(pos)
                                                mostrarMensaje("Título actualizado.")
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("PaletteAdapter", "Error al actualizar título", e)
                                            mostrarMensaje("Error al actualizar título.")
                                        }
                                }
                            } else {
                                mostrarMensaje("El título no puede estar vacío.")
                            }
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }

                binding.deleteButton.setOnClickListener {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Eliminar paleta")
                        .setMessage("¿Estás seguro de que quieres eliminar esta paleta?")
                        .setPositiveButton("Sí") { _, _ ->
                            // Elimina en Firestore
                            firebaseUser?.let { user ->
                                db.collection("users")
                                    .document(user.uid)
                                    .collection("palettes")
                                    .document(palette.id)
                                    .delete()
                                    .addOnSuccessListener {
                                        // Elimina de la lista local y notifica la eliminación
                                        val pos = palettes.indexOfFirst { it.id == palette.id }
                                        if (pos != -1) {
                                            palettes.removeAt(pos)
                                            notifyItemRemoved(pos)
                                            mostrarMensaje("Paleta eliminada.")
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("PaletteAdapter", "Error al eliminar paleta", e)
                                        mostrarMensaje("Error al eliminar paleta.")
                                    }
                            }
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }

                // Listener para el botón de Spotify (ahora abre un enlace a playlist)
                binding.spotifyButton.setOnClickListener {
                    Log.d("SpotifyLink", "Botón de Spotify pulsado para paleta: ${palette.title}") // Log de depuración

                    // Obtiene la URL de la playlist basada en la paleta usando la nueva lógica
                    val playlistUrl = getPlaylistUrlForPalette(palette)

                    if (playlistUrl != null) {
                        // Abre la URL en la aplicación de Spotify o en el navegador
                        openUrlInSpotify(playlistUrl)
                    } else {
                        mostrarMensaje("No hay playlist asociada a esta paleta.")
                        Log.d("SpotifyLink", "No se encontró URL de playlist para la paleta: ${palette.title}")
                    }
                }

                // Listener para el clic en el item completo de la paleta
                binding.root.setOnClickListener {
                    val fragment = SimilarImagesFragment().apply {
                        arguments = Bundle().apply {
                            putStringArrayList("colors", ArrayList(palette.colors))
                        }
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null) // Permite volver al fragmento anterior
                        .commit()
                }
            }
        }
    }

    // Función para obtener la URL de la playlist basada en la paleta
    // Implementa aquí la lógica para mapear paletas a URLs
    private fun getPlaylistUrlForPalette(palette: PaletteData): String? {
        // --- LÓGICA DE MAPEO DE COLORES A PLAYLISTS ---

        // Ejemplo 1: Mapeo simple por título (lo que tenías antes, pero mejorado con toLowerCase)
        // return playlistMap[palette.title.toLowerCase()]

        // Ejemplo 2: Mapeo basado en un "estado de ánimo" derivado de los colores
        val colorMood = determineColorMood(palette.colors)
        Log.d("SpotifyLink", "Estado de ánimo de color determinado: $colorMood")
        return moodPlaylistMap[colorMood] ?: DEFAULT_PLAYLIST_URL // Retorna la URL del mapeo o la por defecto

        // Puedes combinar lógicas, por ejemplo: intentar mapear por título, si no funciona,
        // intentar mapear por estado de ánimo de color, y si tampoco, usar la por defecto.
        /*
        val urlByTitle = playlistMap[palette.title.toLowerCase()]
        if (urlByTitle != null) {
            return urlByTitle
        } else {
            val colorMood = determineColorMood(palette.colors)
            return moodPlaylistMap[colorMood] ?: DEFAULT_PLAYLIST_URL
        }
        */
    }

    // Función para determinar un "estado de ánimo" o tema musical basado en los colores de la paleta
    // *** DEBES IMPLEMENTAR LA LÓGICA REAL DE ANÁLISIS DE COLORES AQUÍ ***
    private fun determineColorMood(colors: List<String>): String {
        // Esta es una implementación de ejemplo MUY BÁSICA.
        // Deberías analizar los colores (ej. valores HSL, dominancia) para determinar un estado de ánimo.

        var hasWarmColor = false // Colores como rojo, naranja, amarillo
        var hasCoolColor = false // Colores como azul, verde, violeta
        var hasDarkColor = false // Colores oscuros
        var hasBrightColor = false // Colores brillantes

        for (hexColor in colors) {
            try {
                val color = Color.parseColor(hexColor)
                val hsv = FloatArray(3)
                Color.colorToHSV(color, hsv)
                val hue = hsv[0] // Tono (0-360)
                val saturation = hsv[1] // Saturación (0-1)
                val value = hsv[2] // Brillo/Valor (0-1)

                // Lógica simple para identificar tipos de colores
                if (hue >= 0 && hue < 60 || hue >= 300 && hue <= 360) hasWarmColor = true // Rojo a amarillo, y violeta a rojo
                if (hue >= 120 && hue < 240) hasCoolColor = true // Verde a azul
                if (value < 0.4) hasDarkColor = true // Valor bajo = oscuro
                if (saturation > 0.6 && value > 0.6) hasBrightColor = true // Alta saturación y brillo = brillante

            } catch (e: IllegalArgumentException) {
                Log.e("ColorAnalysis", "Color hexadecimal inválido al analizar: $hexColor", e)
                // Ignorar colores inválidos o manejarlos según necesites
            }
        }

        // Lógica de ejemplo para determinar el estado de ánimo
        if (hasBrightColor && hasWarmColor && !hasCoolColor) return "Feliz"
        if (hasCoolColor && !hasWarmColor && !hasDarkColor) return "Tranquilo"
        if (hasDarkColor && (hasCoolColor || hasWarmColor)) return "Misterioso"
        if (hasDarkColor && !hasCoolColor && !hasWarmColor) return "Melancólico" // O algún otro estado de ánimo oscuro
        if (hasWarmColor && hasBrightColor) return "Energético"

        // Retorna un estado de ánimo por defecto si no coincide con ninguna regla
        return "Tranquilo" // O el estado de ánimo que consideres por defecto
    }


    // Función para abrir una URL en la aplicación de Spotify o en el navegador
    private fun openUrlInSpotify(url: String) {
        try {
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            // Opcional: Intentar abrir específicamente en la app de Spotify
            // intent.setPackage("com.spotify.music") // Descomentar si quieres forzar la apertura en la app

            startActivity(intent)
            Log.d("SpotifyLink", "Intent de abrir URL lanzado: $url")
        } catch (e: Exception) {
            Log.e("SpotifyLink", "Error al intentar abrir URL: $url", e)
            mostrarMensaje("No se pudo abrir el enlace de la playlist. Asegúrate de tener la app de Spotify instalada.")
        }
    }

    // Función de utilidad para mostrar mensajes Toast en el hilo principal
    private fun mostrarMensaje(mensaje: String) {
        // Asegurarse de que estamos en el contexto de la actividad o fragmento antes de mostrar el Toast
        if (isAdded && activity != null) {
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
