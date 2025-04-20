package com.example.pictopalette

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private lateinit var gridView: GridView
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

        // Configurar el GridView
        val adapter = ProfilePicsAdapter(requireContext(), profilePics)
        gridView.adapter = adapter

        gridView.setOnItemClickListener { _, _, position, _ ->
            val selectedPic = profilePics[position]

            listener?.onProfilePicSelected(selectedPic)
            // Mostrar mensaje de confirmación
            Toast.makeText(requireContext(), "Foto seleccionada: $selectedPic", Toast.LENGTH_SHORT).show()
        }

        return view
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
