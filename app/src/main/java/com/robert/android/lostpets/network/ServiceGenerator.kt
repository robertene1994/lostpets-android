package com.robert.android.lostpets.network

import android.content.Context
import com.robert.android.lostpets.BuildConfig
import com.robert.android.lostpets.network.converter.GsonSerializer
import com.robert.android.lostpets.network.converter.NullOrEmptyConverterFactory
import com.robert.android.lostpets.network.security.AuthenticationInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Clase que genera los servicios de la API disponibles para los usuarios.
 *
 * @author Robert Ene
 */
class ServiceGenerator {

    companion object {
        const val API_BASE_URL = BuildConfig.LOST_PETS_API
        private const val CONNECT_TIMEOUT_SEC = 5L
        private const val READ_TIMEOUT_SEC = 5L
        private const val WRITE_TIMEOUT_SEC = 5L

        private var retrofitBuilder: Retrofit.Builder? = null

        /**
         * Método que crea un determinado servicio.
         *
         * @param <S> el tipo del servicio que se crea.
         * @param context el contexto de la aplicación.
         * @param serviceClass la clase del servicio que se crea.
         * @return el servicio creado.
         */
        fun <S> createService(context: Context, serviceClass: Class<S>): S {
            if (retrofitBuilder == null)
                setUp(context)
            return retrofitBuilder!!.build().create(serviceClass)
        }

        private fun setUp(context: Context) {
            val interceptor = AuthenticationInterceptor(context)

            val httpClientBuilder = OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT_SEC, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT_SEC, TimeUnit.SECONDS)

            if (!httpClientBuilder.interceptors().contains(interceptor))
                httpClientBuilder.addInterceptor(interceptor)

            retrofitBuilder = Retrofit.Builder().baseUrl(API_BASE_URL)
                    .client(httpClientBuilder.build())
                    // .addConverterFactory(NullOrEmptyConverterFactory())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(GsonSerializer.instance))
        }
    }
}
