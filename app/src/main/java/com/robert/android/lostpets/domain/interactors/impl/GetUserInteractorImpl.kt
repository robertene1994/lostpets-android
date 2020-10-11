package com.robert.android.lostpets.domain.interactors.impl

import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.GetUserInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.domain.repository.SessionRepository

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz GetUserInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.GetUserInteractor
 */
class GetUserInteractorImpl(executor: Executor, mainThread: MainThread,
                            callback: GetUserInteractor.Callback,
                            sessionRepository: SessionRepository)
    : AbstractInteractor(executor, mainThread), GetUserInteractor {

    private val mCallback: GetUserInteractor.Callback = callback
    private val mSessionRepository: SessionRepository = sessionRepository

    override fun run() {
        val user = mSessionRepository.getUser()
        if (user != null)
            mMainThread.post(Runnable { mCallback.onUserRetrieved(user) })
    }
}
