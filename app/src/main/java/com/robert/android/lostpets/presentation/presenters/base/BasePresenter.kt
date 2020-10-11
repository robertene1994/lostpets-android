package com.robert.android.lostpets.presentation.presenters.base

/**
 * Interfaz base para todos los presenters que contiene los principales métodos del ciclo de vida
 * de una activity.
 *
 * @author Robert Ene
 */
interface BasePresenter {

    /**
     * Método que controla el ciclo de vida de la vista. Se corresponde al método onResume() de
     * una activity/fragment.
     */
    fun resume()

    /**
     * Método que controla el ciclo de vida de la vista. Se corresponde al método onPause() de
     * una activity/fragment.
     */
    fun pause()

    /**
     * Método que controla el ciclo de vida de la vista. Se corresponde al método onStop() de
     * una activity/fragment.
     */
    fun stop()

    /**
     * Método que controla el ciclo de vida de la vista. Se corresponde al método onDestroy() de
     * una activity/fragment.
     */
    fun destroy()
}