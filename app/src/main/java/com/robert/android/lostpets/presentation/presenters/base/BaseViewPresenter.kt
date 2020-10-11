package com.robert.android.lostpets.presentation.presenters.base

/**
 * Interfaz base para las interfaces View de los presenters. Contiene métodos compartidos por
 * las interfaces View de los presenters.
 *
 * @author Robert Ene
 */
interface BaseViewPresenter {
    /**
     * Método que muestra progreso durante el tiempo en el que la aplicación está realizando
     * operaciones en background.
     */
    fun showProgress()

    /**
     * Método que oculta el progreso cuando la aplicación finaliza las operaciones en background.
     */
    fun hideProgress()

    /**
     * Método que notifica que el dispositivo móvil no tiene acceso a Internet.
     */
    fun noInternetConnection()

    /**
     * Método que notifica que el servico (API) no está disponible por cualquier razón.
     */
    fun serviceNotAvailable()
}
