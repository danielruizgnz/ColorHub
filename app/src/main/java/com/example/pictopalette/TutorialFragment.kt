package com.example.pictopalette

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout // Importar LinearLayout
import android.widget.TextView // Importar TextView
import androidx.fragment.app.Fragment
import com.example.pictopalette.databinding.FragmentTutorialBinding // Asegúrate de tener este binding si usas View Binding

class TutorialFragment : Fragment() {

    // Si usas View Binding, declara tu binding aquí
    private var _binding: FragmentTutorialBinding? = null
    private val binding get() = _binding!!

    // Referencias a las cabeceras y contenidos de las secciones
    private lateinit var sectionHeaders: List<LinearLayout>
    private lateinit var sectionContents: List<TextView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout para este fragmento
        // Si usas View Binding:
        _binding = FragmentTutorialBinding.inflate(inflater, container, false)
        return binding.root

        // Si no usas View Binding:
        // return inflater.inflate(R.layout.fragment_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener referencias a las cabeceras y contenidos de las secciones
        // Si usas View Binding:
        sectionHeaders = listOf(
            binding.section1Header,
            binding.section2Header,
            binding.section3Header,
            binding.section4Header,
            binding.section5Header,
            binding.section6Header,
            binding.section7Header,
            binding.section8Header,
            binding.section9Header,
            binding.section10Header
        )

        sectionContents = listOf(
            binding.section1Content,
            binding.section2Content,
            binding.section3Content,
            binding.section4Content,
            binding.section5Content,
            binding.section6Content,
            binding.section7Content,
            binding.section8Content,
            binding.section9Content,
            binding.section10Content
        )

        // Si no usas View Binding, usa findViewById:
        /*
        sectionHeaders = listOf(
            view.findViewById(R.id.section1Header),
            view.findViewById(R.id.section2Header),
            // ... añadir todas las cabeceras
        )

        sectionContents = listOf(
            view.findViewById(R.id.section1Content),
            view.findViewById(R.id.section2Content),
            // ... añadir todos los contenidos
        )
        */


        // Configurar OnClickListener para cada cabecera
        for (i in sectionHeaders.indices) {
            sectionHeaders[i].setOnClickListener {
                // Alternar la visibilidad del contenido correspondiente
                if (sectionContents[i].visibility == View.GONE) {
                    sectionContents[i].visibility = View.VISIBLE
                } else {
                    sectionContents[i].visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpia la referencia al binding cuando la vista se destruye
    }
}
