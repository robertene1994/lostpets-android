package com.robert.android.lostpets.network.service

import com.robert.android.lostpets.domain.model.Ad
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Interfaz que contiene las operaciones de los anuncios de las mascotas perdidas, mediante las
 * cuales la aplicación se comunica con la API.
 *
 * @author Robert Ene
 */
interface AdService {

    /**
     * Método que recupera todos los anuncios de mascotas perdidas.
     *
     * @return la llamada asíncrona a la API.
     */
    @GET("ad")
    fun getAds(): Call<List<Ad>>

    /**
     * Método que recupera todos los anuncios de mascotas perdidas perteneciendes a un
     * determinado usuario.
     *
     * @param userId el id del usuario.
     * @return la llamada asíncrona a la API.
     */
    @GET("ad/user/{userId}")
    fun getUserAds(@Path("userId") userId: Long): Call<List<Ad>>

    /**
     * Método que publica un nuevo anuncio de una mascota perdida.
     *
     * @param ad el anuncio de la mascota perdida.
     * @param image la imagen de la mascota perdida.
     * @return la llamada asíncrona a la API.
     */
    @Multipart
    @POST("ad")
    fun saveAd(@Part("ad") ad: Ad, @Part image: MultipartBody.Part): Call<Boolean>

    /**
     * Método que modifica un anuncio existente de una mascota perdida.
     *
     * @param ad el anuncio de la mascota perdida.
     * @param image la imagen de la mascota perdida.
     * @return la llamada asíncrona a la API.
     */
    @Multipart
    @PUT("ad")
    fun updateAd(@Part("ad") ad: Ad, @Part image: MultipartBody.Part?): Call<Boolean>
}
