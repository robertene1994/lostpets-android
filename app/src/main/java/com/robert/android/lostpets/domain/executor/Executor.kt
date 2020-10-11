package com.robert.android.lostpets.domain.executor

import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor

/**
 * Interfaz Executor responsable de la ejecución de los interactors en los hilos backgorund.
 *
 * @author Robert Ene
 */
interface Executor {

    /**
     * Método que ejecuta la lógica del interactor.
     *
     * @param interactor el interactor que se ejecuta.
     */
    fun execute(interactor: AbstractInteractor)
}
