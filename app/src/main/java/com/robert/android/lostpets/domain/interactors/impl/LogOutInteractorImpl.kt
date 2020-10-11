package com.robert.android.lostpets.domain.interactors.impl

import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.LogOutInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.domain.repository.SessionRepository

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz LogOutInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.LogOutInteractor
 */
class LogOutInteractorImpl(
    executor: Executor,
    mainThread: MainThread,
    callback: LogOutInteractor.Callback,
    sessionRepository: SessionRepository
) :
    AbstractInteractor(executor, mainThread), LogOutInteractor {

    private val mCallback: LogOutInteractor.Callback = callback
    private val mSessionRepository: SessionRepository = sessionRepository

    override fun run() {
        mSessionRepository.deleteUser()
        mSessionRepository.deleteToken()
        mMainThread.post(Runnable { mCallback.onLogOut() })
    }
}
