package com.example.pictopalette

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment


class HomeFragment : Fragment() {

    private lateinit var gridView: GridView
    private lateinit var switchDarkMode: Switch // Referencia al Switch
    private lateinit var textViewDarkModeLabel: TextView // Referencia al TextView del label

    private val profilePics = arrayOf(
        R.drawable.goya, R.drawable.davinci, R.drawable.velazquez,
        R.drawable.monet, R.drawable.vangogh
    )

    interface OnProfilePicSelectedListener {
        fun onProfilePicSelected(imageResource: Int)
    }

    private var listener: OnProfilePicSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        gridView = view.findViewById(R.id.gridViewProfilePics)
        switchDarkMode = view.findViewById(R.id.switchDarkMode) // Obtener referencia al Switch
        textViewDarkModeLabel = view.findViewById(R.id.textViewDarkModeLabel) // Obtener referencia al TextView

        // Configurar el GridView
        val adapter = ProfilePicsAdapter(requireContext(), profilePics)
        gridView.adapter = adapter

        gridView.setOnItemClickListener { _, _, position, _ ->
            val selectedPic = profilePics[position]

            listener?.onProfilePicSelected(selectedPic)
            // Mostrar mensaje de confirmación
            Toast.makeText(requireContext(), "Foto seleccionada", Toast.LENGTH_SHORT).show() // Mensaje más genérico
        }

        // === Lógica para el Modo Oscuro ===

        // Obtener SharedPreferences para guardar/cargar el estado del modo oscuro
        val sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        val isDarkModeEnabled = sharedPreferences.getBoolean("DARK_MODE_ENABLED", false) // false es el valor por defecto

        // Establecer el estado inicial del Switch
        switchDarkMode.isChecked = isDarkModeEnabled

        // Aplicar el tema inicialmente basado en la preferencia guardada
        applyDarkModeTheme(isDarkModeEnabled)


        // Configurar el listener para el Switch
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            // Guardar la preferencia del usuario
            with(sharedPreferences.edit()) {
                putBoolean("DARK_MODE_ENABLED", isChecked)
                apply() // Usar apply() para guardar de forma asíncrona
            }

            // Aplicar el tema
            applyDarkModeTheme(isChecked)

            // Opcional: Mostrar un mensaje al usuario
            if (isChecked) {
                Toast.makeText(requireContext(), "Modo Oscuro Activado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Modo Oscuro Desactivado", Toast.LENGTH_SHORT).show()
            }
        }

        // ==================================

        return view
    }

    // Función para aplicar el tema oscuro o claro
    private fun applyDarkModeTheme(isDarkModeEnabled: Boolean) {
        if (isDarkModeEnabled) {
            // Aplicar modo oscuro
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            // Aplicar modo claro
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        // Nota: Cambiar el modo nocturno por defecto recrea la actividad.
        // Esto es el comportamiento estándar para aplicar el tema.
    }


    // Asegurarse de que la actividad implementa el listener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as OnProfilePicSelectedListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnProfilePicSelectedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}
