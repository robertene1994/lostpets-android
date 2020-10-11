package com.robert.android.lostpets.network

import android.content.Context
import com.robert.android.lostpets.BuildConfig
import com.robert.android.lostpets.domain.utils.Jwt
import com.robert.android.lostpets.storage.SessionRepositoryImpl
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.StompHeader
import java.util.*

/**
 * Clase que genera los servicios de mensajería basados en la comunicación mediante el protocolo
 * STOMP.
 *
 * @author Robert Ene
 */
class MessagingServiceGenerator {

    companion object {
        private const val MESSAGING_SERVICE_BASE_URL = BuildConfig.LOST_PETS_MESSAGING
        private const val CLIENT_HEARTBEAT_MS = 1000
        private const val SERVER_HEARTBEAT_MS = 1000

        /**
         * Método que crea el servicio STOMP.
         *
         * @param context el contexto de la aplicación.
         * @return el servicio STOMP creado.
         */
        fun createStompClient(context: Context): StompClient {
            return setUp(context)
        }

        private fun setUp(context: Context): StompClient {
            val mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP,
                    MESSAGING_SERVICE_BASE_URL)
                    .withClientHeartbeat(CLIENT_HEARTBEAT_MS)
                    .withServerHeartbeat(SERVER_HEARTBEAT_MS)

            val token = SessionRepositoryImpl(context).getToken()
            val headers = ArrayList<StompHeader>()
            if (token != null)
                headers.add(StompHeader("Authorization", token))

            mStompClient.connect(headers)
            return mStompClient
        }
    }
}
