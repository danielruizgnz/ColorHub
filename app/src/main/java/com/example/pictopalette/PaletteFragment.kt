package com.example.pictopalette

import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.example.pictopalette.databinding.FragmentPaletteBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PaletteFragment : Fragment() {

    private lateinit var binding: FragmentPaletteBinding
    private val firebaseUser get() = FirebaseAuth.getInstance().currentUser

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            binding.imageView.setImageBitmap(bitmap)
            extractColorsFromBitmap(bitmap)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPaletteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        firebaseUser?.let { user ->
//            binding.tvUserName.text = user.displayName ?: "Usuario"
//            Glide.with(this)
//                .load(user.photoUrl)
//                .circleCrop()
//                .into(binding.ivUserProfile)
//        }

        binding.btnPickImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSignOut.setOnClickListener {
            Firebase.auth.signOut()
            GoogleSignIn.getClient(
                requireContext(),
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            ).signOut()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }

        binding.btnSavePalette.setOnClickListener {
            showTitleInputDialog()
        }
    }

    private fun extractColorsFromBitmap(bitmap: Bitmap) {
        Palette.from(bitmap).generate { palette ->
            palette?.let {
                val colors = listOf(
                    it.getVibrantColor(Color.GRAY),
                    it.getMutedColor(Color.GRAY),
                    it.getDarkVibrantColor(Color.GRAY),
                    it.getDarkMutedColor(Color.GRAY),
                    it.getLightVibrantColor(Color.GRAY)
                )
                Log.d("PaletteColors", "Colores extraídos: $colors")
                updateUIColor(colors)
            }
        }
    }

    private fun updateUIColor(colors: List<Int>) {
        binding.colorPalette.removeAllViews()
        for (color in colors) {
            val colorView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(0, 100, 1f)
                setBackgroundColor(color)
            }
            binding.colorPalette.addView(colorView)
        }
    }

    private fun showTitleInputDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Título de la paleta"

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Guardar Paleta")
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                val title = editText.text.toString().ifBlank { "Sin título" }
                savePaletteToFirestore(title)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun savePaletteToFirestore(title: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) return

        val paletteColors = mutableListOf<String>()
        for (i in 0 until binding.colorPalette.childCount) {
            val view = binding.colorPalette.getChildAt(i)
            val color = (view.background as? android.graphics.drawable.ColorDrawable)?.color
            color?.let {
                paletteColors.add(String.format("#%06X", 0xFFFFFF and it))
            }
        }

        if (paletteColors.isEmpty()) return

        val paletteData = mapOf(
            "title" to title,
            "colors" to paletteColors,
            "timestamp" to System.currentTimeMillis()
        )

        val db = Firebase.firestore
        db.collection("users")
            .document(user.uid)
            .collection("palettes")
            .add(paletteData)
            .addOnSuccessListener {
                Log.d("Firestore", "Paleta guardada correctamente")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al guardar paleta", e)
            }
    }
}