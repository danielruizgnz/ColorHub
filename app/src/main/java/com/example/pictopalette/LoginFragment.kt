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
import com.google.firebase.auth.userProfileChangeRequest

class LoginFragment : Fragment() {

    private lateinit var btnLogin: Button
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etName: EditText
    private lateinit var txtStatus: TextView
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout
        val binding = FragmentLoginBinding.inflate(inflater, container, false)

        btnLogin = binding.btnLogin
        etName = binding.etName
        etEmail = binding.etEmail
        etPassword = binding.etPassword
        txtStatus = binding.txtStatus

        firebaseAuth = FirebaseAuth.getInstance()

        // Configura el evento al hacer click en el botón de login
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val name = etName.text.toString().trim()

            // Verificar si los campos no están vacíos
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUserOrRegister(email, password, name)
            } else {
                txtStatus.text = "Por favor ingresa todos los campos"
            }
        }

        return binding.root
    }

    private fun loginUserOrRegister(email: String, password: String, name: String) {
        Log.d("LoginFragment", "Iniciando login con email: $email")

        // Intentar iniciar sesión con correo y contraseña
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginFragment", "Login exitoso")
                    // Si el inicio de sesión fue exitoso
                    val user = firebaseAuth.currentUser
                    val displayName = user?.displayName ?: "Usuario sin nombre" // Si no se introduce nombre

                    txtStatus.text = "Bienvenido, $displayName"

                    // Llamar a updateNavHeader() para actualizar el encabezado de la navegación
                    val mainActivity = activity as MainActivity
                    mainActivity.updateNavHeader()

                    // Redirigir al usuario a otro fragmento (Ejemplo: PaletteFragment)
                    mainActivity.replaceFragment(PaletteFragment())  // Cambiar al fragmento de paletas
                } else {
                    Log.e("LoginFragment", "Error en login: ${task.exception?.message}")
                    // Si el inicio de sesión falla (por ejemplo, si el usuario no existe)
                    Log.w("LoginFragment", "Usuario no encontrado, creando cuenta", task.exception)
                    createUser(email, password, name)
                }
            }
    }

    private fun createUser(email: String, password: String, name: String) {
        // Crear un nuevo usuario si no existe
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Si el registro fue exitoso
                    val user = firebaseAuth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name  // Establecer el nombre del usuario
                    }

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                        if (profileTask.isSuccessful) {
                            Log.d("LoginFragment", "Nombre de usuario actualizado")
                        }
                    }

                    txtStatus.text = "Registro exitoso, bienvenido, ${user?.displayName}"

                    // Llamar a updateNavHeader() para actualizar el encabezado de la navegación
                    val mainActivity = activity as MainActivity
                    mainActivity.updateNavHeader()

                    // Redirigir al usuario a otro fragmento (Ejemplo: PaletteFragment)
                    mainActivity.replaceFragment(PaletteFragment())  // Cambiar al fragmento de paletas
                } else {
                    // Si hubo un error al crear la cuenta
                    Log.w("LoginFragment", "Error en el registro", task.exception)
                    txtStatus.text = "Error al crear cuenta: ${task.exception?.localizedMessage}"
                }
            }
    }
}