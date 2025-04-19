package com.example.pictopalette

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritePalettesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        paletteAdapter = PaletteAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = paletteAdapter
        }

        firebaseUser?.let { user ->
            db.collection("users")
                .document(user.uid)
                .collection("palettes")
                .get()
                .addOnSuccessListener { documents ->
                    val palettes = documents.mapNotNull { doc ->
                        val palette = doc.toObject(PaletteData::class.java)
                        palette.id = doc.id
                        palette
                    }
                    paletteAdapter.submitList(palettes)
                }
        }
    }

    data class PaletteData(
        var id: String = "",
        val title: String = "",
        val colors: List<String> = emptyList(),
        val timestamp: Long = 0
    )

    inner class PaletteAdapter : RecyclerView.Adapter<PaletteAdapter.PaletteViewHolder>() {
        private val palettes = mutableListOf<PaletteData>()

        fun submitList(list: List<PaletteData>) {
            palettes.clear()
            palettes.addAll(list)
            notifyDataSetChanged()
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

        inner class PaletteViewHolder(private val binding: ItemPaletteBinding) :
            RecyclerView.ViewHolder(binding.root) {

            fun bind(palette: PaletteData) {
                binding.paletteTitleTextView.text = palette.title
                binding.paletteColorsLayout.removeAllViews()

                palette.colors.forEach { hexColor ->
                    val colorView = View(requireContext()).apply {
                        layoutParams = LinearLayout.LayoutParams(0, 100, 1f)
                        setBackgroundColor(Color.parseColor(hexColor))
                    }
                    binding.paletteColorsLayout.addView(colorView)
                }

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
                                val updatedPalette = palette.copy(title = newTitle)
                                firebaseUser?.let { user ->
                                    db.collection("users")
                                        .document(user.uid)
                                        .collection("palettes")
                                        .document(palette.id)
                                        .update("title", newTitle)
                                        .addOnSuccessListener {
                                            val pos = palettes.indexOfFirst { it.id == palette.id }
                                            if (pos != -1) {
                                                palettes[pos] = updatedPalette
                                                notifyItemChanged(pos)
                                            }
                                        }
                                }
                            }
                        }
                        .setNegativeButton("Cancelar", null)
                        .show()
                }
            }
        }
    }
}