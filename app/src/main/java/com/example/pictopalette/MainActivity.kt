package com.example.pictopalette

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pictopalette.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Verificar si ya hay una sesión iniciada
        if (firebaseAuth.currentUser == null) {
            // Si no hay usuario logueado, mostrar LoginFragment
            replaceFragment(LoginFragment())
        } else {
            // Si hay un usuario logueado, mostrar PaletteFragment
            replaceFragment(PaletteFragment())
        }

        // Aquí podrías agregar el NavigationDrawer si es necesario
        // y otros componentes como el NavigationView, etc.
    }

    // Función para reemplazar el fragmento actual
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    // Puedes agregar el código para manejar el cierre de sesión aquí, si lo necesitas.
}
