package com.example.pictopalette

import android.app.Activity
import android.content.Intent
import android.net.Uri // Importar Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class SpotifyAuthManager(private val fragment: Fragment) {

    // *** REEMPLAZA ESTOS VALORES CON TUS CREDENCIALES DE SPOTIFY DEVELOPERS ***
    private val clientId = "6ba440606f8246938c418af779e34521" // Tu Client ID
    // *** IMPORTANTE: ESTA REDIRECT URI DEBE COINCIDIR EXACTAMENTE CON LA REGISTRADA EN SPOTIFY DEVELOPERS ***
    // Un esquema personalizado como "tuapp://callback" es común para Android.
    private val redirectUri = "pictopalette://callback" // *** REEMPLAZA CON TU REDIRECT URI ***

    // Scopes necesarios para crear y modificar playlists
    private val scopes = arrayOf("playlist-modify-public", "playlist-modify-private", "user-read-private") // Añadimos user-read-private para obtener el ID de usuario

    // Eliminamos el authResultLauncher y la lógica de manejo de resultados de aquí.
    // El manejo de resultados se hace en el Fragmento que lanza el Intent.

    // Función para obtener el Intent necesario para iniciar la actividad de login de Spotify
    fun getLoginIntent(): Intent {
        // Construye la solicitud de autorización
        val builder = AuthorizationRequest.Builder(
            clientId,
            AuthorizationResponse.Type.TOKEN, // Solicitamos un token de acceso
            redirectUri
        )

        // Establece los scopes (permisos) solicitados
        builder.setScopes(scopes)

        val request = builder.build()

        // Crea y retorna el Intent para iniciar la actividad de login
        return AuthorizationClient.createLoginActivityIntent(
            fragment.requireContext() as Activity, // Se necesita una Activity para lanzar el Intent
            request
        )
    }
}
