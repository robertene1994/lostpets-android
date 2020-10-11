package com.robert.android.lostpets.threading

import android.os.Handler
import android.os.Looper
import com.robert.android.lostpets.domain.executor.MainThread

/**
 * Clase que implementa la interfaz MainThread. Su objetivo es que las operaciones de los
 * resultados de los interactors se ejecuten en el hilo principal de la aplicación.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.executor.MainThread
 */
class MainThreadImpl: MainThread {

    companion object {
        val instance: MainThread by lazy { MainThreadImpl()  }
    }

    private val mHandler: Handler = Handler(Looper.getMainLooper())

    override fun post(runnable: Runnable) {
        mHandler.post(runnable)
    }
}
