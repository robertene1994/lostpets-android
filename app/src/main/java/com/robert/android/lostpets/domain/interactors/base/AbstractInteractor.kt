package com.robert.android.lostpets.domain.interactors.base

import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread

/**
 * Clase base para todos los interactors que contienen la lógica de la aplicación.
 *
 * @author Robert Ene
 */
abstract class AbstractInteractor(executor: Executor, mainThread: MainThread): Interactor {

    private val mExecutor: Executor = executor
    protected val mMainThread: MainThread = mainThread

    /**
     * Método que contiene la lógica del interactor. Su propósito es para pruebas unitarias o de
     * integración. En la práctica, los interactors ejecutan su lógica mediante el método execute().
     */
    abstract fun run()

    override fun execute() {
        mExecutor.execute(this)
    }
}
