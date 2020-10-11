package com.robert.android.lostpets.utilTest.threading

import com.robert.android.lostpets.domain.executor.MainThread

/**
 * Clase que implementa la interfaz MainThread. Se utiliza para las pruebas.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.executor.MainThread
 */
class TestMainThread : MainThread {

    override fun post(runnable: Runnable) {
        runnable.run()
    }
}