package com.example.pictopalette

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {
    @GET("search/photos")
    fun searchPhotos(
        @Query("query") query: String,
        @Query("client_id") clientId: String,
        @Query("per_page") perPage: Int = 20
    ): Call<UnsplashResponse>
}

data class UnsplashResponse(
    val results: List<UnsplashPhoto>
)

data class UnsplashPhoto(
    val id: String,
    val urls: Urls
)

data class Urls(
    val small: String
)