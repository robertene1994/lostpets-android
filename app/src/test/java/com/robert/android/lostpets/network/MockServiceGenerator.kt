package com.robert.android.lostpets.network

import com.robert.android.lostpets.BuildConfig
import com.robert.android.lostpets.network.converter.GsonSerializer
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * Clase que genera mocks para los servicios de la API disponibles para los usuarios.
 *
 * @author Robert Ene
 */
class MockServiceGenerator {

    companion object {
        private const val API_BASE_URL_TEST = BuildConfig.LOST_PETS_API_TEST
        private const val CONNECT_TIMEOUT_SEC = 5L
        private const val READ_TIMEOUT_SEC = 5L
        private const val WRITE_TIMEOUT_SEC = 5L

        private var retrofitBuilder: Retrofit.Builder? = null

        /**
         * Método que crea un mock para un servicio determinado.
         *
         * @param <S> el tipo del servicio que se crea.
         * @param mockWebServer el servidor que sirve de mock para las peticiones realizadas.
         * @param serviceClass la clase del servicio que se crea.
         * @return el mock del servicio creado.
         */
        fun <S> createService(mockWebServer: MockWebServer, serviceClass: Class<S>): S {
            if (retrofitBuilder == null) {
                setUp(mockWebServer)
            }
            return retrofitBuilder!!.build().create(serviceClass)
        }

        private fun setUp(mockWebServer: MockWebServer) {
            val httpClientBuilder = OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT_SEC, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT_SEC, TimeUnit.SECONDS)

            retrofitBuilder = Retrofit.Builder().baseUrl(mockWebServer.url(API_BASE_URL_TEST))
                    .client(httpClientBuilder.build())
                    // .addConverterFactory(NullOrEmptyConverterFactory())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(GsonSerializer.instance))
        }
    }
}
