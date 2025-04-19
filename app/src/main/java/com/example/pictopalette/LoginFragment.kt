package com.example.pictopalette

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pictopalette.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var btnLogin: Button
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var txtStatus: TextView
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout
        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        btnLogin = binding.btnLogin
        etEmail = binding.etEmail
        etPassword = binding.etPassword
        txtStatus = binding.txtStatus

        firebaseAuth = FirebaseAuth.getInstance()

        // Configura el evento al hacer click en el botón de login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Verificar si los campos no están vacíos
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUserOrRegister(email, password)
            } else {
                txtStatus.text = "Por favor ingresa todos los campos"
            }
        }

        return binding.root
    }

    private fun loginUserOrRegister(email: String, password: String) {
        // Intentar iniciar sesión con correo y contraseña
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Si el inicio de sesión fue exitoso
                    val user = firebaseAuth.currentUser
                    txtStatus.text = "Bienvenido, ${user?.displayName}"

                    // Redirigir al usuario a otro fragmento (Ejemplo: PaletteFragment)
                    val mainActivity = activity as MainActivity
                    mainActivity.replaceFragment(PaletteFragment())  // Cambiar al fragmento de paletas
                } else {
                    // Si el inicio de sesión falla (por ejemplo, si el usuario no existe)
                    Log.w("LoginFragment", "Usuario no encontrado, creando cuenta", task.exception)
                    createUser(email, password)
                }
            }
    }

    private fun createUser(email: String, password: String) {
        // Crear un nuevo usuario si no existe
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Si el registro fue exitoso
                    val user = firebaseAuth.currentUser
                    txtStatus.text = "Registro exitoso, bienvenido, ${user?.displayName}"

                    // Redirigir al usuario a otro fragmento (Ejemplo: PaletteFragment)
                    val mainActivity = activity as MainActivity
                    mainActivity.replaceFragment(PaletteFragment())  // Cambiar al fragmento de paletas
                } else {
                    // Si hubo un error al crear la cuenta
                    Log.w("LoginFragment", "Error en el registro", task.exception)
                    txtStatus.text = "Error al crear cuenta: ${task.exception?.localizedMessage}"
                }
            }
    }
}