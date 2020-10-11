package com.robert.android.lostpets.domain.utils

import android.content.Context
import android.net.ConnectivityManager

/**
 * Clase de utilidades para la conexión a Internet del dispositivo.
 *
 * @author Robert Ene
 */
class ConnectivityStatus {

    companion object {

        /**
         * Método que devuelve el estado de la conexión a Internet del dispositivo.
         *
         * @param context el contexto de la aplicación.
         * @return el estado de la conexión a Internet del dispositivo.
         */
        fun isInternetConnection(context: Context): Boolean {
            val manager = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val connection = manager.activeNetworkInfo
            return connection != null && connection.isConnected
        }
    }
}
