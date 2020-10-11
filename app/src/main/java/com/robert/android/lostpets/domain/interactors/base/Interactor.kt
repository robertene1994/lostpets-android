package com.robert.android.lostpets.domain.interactors.base

/**
 * Interfaz Interactor que sirve como punto común para la ejecución de los interactors de la
 * aplicación.
 */
interface Interactor {

    /**
     * Método que ejecuta la lógica del interactor.
     */
    fun execute()
}
