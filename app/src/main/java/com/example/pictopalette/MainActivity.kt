package com.example.pictopalette

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.pictopalette.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnProfilePicSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)

        updateNavHeader()

        // Fragment inicial según sesión
        if (firebaseAuth.currentUser == null) {
            replaceFragment(LoginFragment())
        } else {
            replaceFragment(PaletteFragment())  // Aquí carga el fragmento de inicio
        }
    }

    // Función para actualizar la foto de perfil en el NavDrawer
    fun updateProfileImage(imageResource: Int) {
        val headerView = binding.navView.getHeaderView(0)
        val imageViewProfile = headerView.findViewById<ImageView>(R.id.imageViewProfile)

        // Cambiar la imagen con Glide o directamente con setImageResource
        Glide.with(this)
            .load(imageResource)
            .circleCrop()
            .into(imageViewProfile)
    }

    fun updateNavHeader() {
        val headerView = binding.navView.getHeaderView(0)
        val imageViewProfile = headerView.findViewById<ImageView>(R.id.imageViewProfile)
        val textViewName = headerView.findViewById<TextView>(R.id.textViewName)
        val textViewEmail = headerView.findViewById<TextView>(R.id.textViewEmail)

        val user = firebaseAuth.currentUser
        if (user != null) {
            textViewName.text = user.displayName
            textViewEmail.text = user.email
            Glide.with(this)
                .load(user.photoUrl)
                .circleCrop()
                .into(imageViewProfile)
        } else {
            textViewName.text = "Inicie sesión"
            textViewEmail.text = ""
            Glide.with(this)
                .load(R.drawable.ic_launcher_foreground) // Puedes poner una imagen por defecto
                .circleCrop()
                .into(imageViewProfile)
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(
            R.id.fragment_container,
            fragment
        )  // Reemplaza el fragment en el contenedor
        transaction.addToBackStack(null)  // Opcional: añadir a la pila de retroceso
        transaction.commit()
    }


    override fun onNavigationItemSelected(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> replaceFragment(HomeFragment())
            R.id.nav_palette -> replaceFragment(PaletteFragment())
            R.id.nav_favorite_palettes -> replaceFragment(FavoritePalettesFragment())  // Aquí agregamos el fragmento de Paletas Favoritas
            R.id.nav_logout -> {
                FirebaseAuth.getInstance().signOut()
                GoogleSignIn.getClient(
                    this,
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                ).signOut()

                updateNavHeader()

                replaceFragment(LoginFragment())
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onProfilePicSelected(imageResource: Int) {
        // Actualizar la imagen de perfil con la imagen seleccionada
        updateProfileImage(imageResource)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}