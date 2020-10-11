package com.robert.android.lostpets.network.security

import android.content.Context
import com.robert.android.lostpets.domain.repository.SessionRepository
import com.robert.android.lostpets.domain.repository.SettingsRepository
import com.robert.android.lostpets.storage.SessionRepositoryImpl
import com.robert.android.lostpets.storage.SettingsRepositoryImpl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Clase que implementa la interfaz Interceptor y que se encarga de añadir las cabeceras
 * necesarias a las peticiones realizadas por el dispositivo del usuario.
 *
 * @author Robert Ene
 */
class AuthenticationInterceptor(context: Context) : Interceptor {

    private val mSessionRepository: SessionRepository = SessionRepositoryImpl(context)
    private val mSettingsRepository: SettingsRepository = SettingsRepositoryImpl(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = mSessionRepository.getToken()
        val language = mSettingsRepository.getLanguage()
        var builder: Request.Builder = chain.request().newBuilder()

        if (token != null)
            builder = builder.addHeader("Authorization", token)

        if (language != null)
            builder = builder.addHeader("Accept-Language", language)

        return chain.proceed(builder.build())
    }
}
