package com.robert.android.lostpets.presentation.presenters.base

import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread

/**
 * Clase base abstracta para todos los presenters que se comunican con interactors.
 *
 * @author Robert Ene
 */
abstract class AbstractPresenter(executor: Executor, mainThread: MainThread) {

    protected var mExecutor: Executor = executor
    protected var mMainThread: MainThread = mainThread
}