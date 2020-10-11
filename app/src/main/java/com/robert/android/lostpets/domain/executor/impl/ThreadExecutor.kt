package com.robert.android.lostpets.domain.executor.impl

import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Clase (singleton) que se encarga de ejecutar la lógica de los interactors en un hilo en
 * background.
 *
 * @author Robert Ene
 */
class ThreadExecutor : Executor {

    companion object {
        private const val CORE_POOL_SIZE = 3
        private const val MAX_POOL_SIZE = 5
        private const val KEEP_ALIVE_TIME = 120L
        private val TIME_UNIT = TimeUnit.SECONDS
        private val WORK_QUEUE: BlockingQueue<Runnable> = LinkedBlockingQueue()

        val mThreadPoolExecutor by lazy {
            ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TIME_UNIT, WORK_QUEUE)
        }
        val instance: Executor by lazy { ThreadExecutor()  }
    }

    override fun execute(interactor: AbstractInteractor) {
        mThreadPoolExecutor.submit { interactor.run() }
    }
}
