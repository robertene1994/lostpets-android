package com.robert.android.lostpets.domain.interactors.base

/**
 * Interfaz base para las interfaces Callback de los interactors. Contiene métodos compartidos
 * por las interfaces Callback de los interactors que se comunican con una API.
 *
 * @author Robert Ene
 */
interface BaseCallbackInteractor {

    /**
     * Método que notifica que el dispositivo móvil no está conectado a Internet.
     */
    fun onNoInternetConnection()

    /**
     * Método que notifica que el servicio (API) con el que se comunica no está disponible por
     * cualquier razón.
     */
    fun onServiceNotAvailable()
}
