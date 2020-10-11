package com.robert.android.lostpets.presentation.presenters.impl

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.GetUserInteractor
import com.robert.android.lostpets.domain.interactors.LogOutInteractor
import com.robert.android.lostpets.domain.interactors.impl.GetUserInteractorImpl
import com.robert.android.lostpets.domain.interactors.impl.LogOutInteractorImpl
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.repository.SessionRepository
import com.robert.android.lostpets.network.message.MessagingService
import com.robert.android.lostpets.presentation.presenters.MainPresenter
import com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter

/**
 * Clase que extiende la clase AbstractPresenter e implementa la interfaz MainPresenter. Implementa
 * las interfaces de los callbacks de los interactors GetUserInteractor y LogOutInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter
 * @see com.robert.android.lostpets.presentation.presenters.MainPresenter
 * @see com.robert.android.lostpets.domain.interactors.GetUserInteractor.Callback
 * @see com.robert.android.lostpets.domain.interactors.LogOutInteractor.Callback
 */
class MainPresenterImpl(
    executor: Executor,
    mainThread: MainThread,
    view: MainPresenter.View,
    context: Context,
    sessionRepository: SessionRepository
) :
    AbstractPresenter(executor, mainThread), MainPresenter, GetUserInteractor.Callback,
        LogOutInteractor.Callback {

    private val mView: MainPresenter.View = view
    private val mContext: Context = context
    private val mSessionRepository: SessionRepository = sessionRepository

    override fun resume() {
        // no aplicable
    }

    override fun pause() {
        // no aplicable
    }

    override fun stop() {
        // no aplicable
    }

    override fun destroy() {
        // no aplicable
    }

    override fun getUser() {
        mView.showProgress()
        GetUserInteractorImpl(mExecutor, mMainThread,
                this, mSessionRepository).execute()
    }

    override fun logOut() {
        mView.showProgress()
        LogOutInteractorImpl(mExecutor, mMainThread,
                this, mSessionRepository).execute()
    }

    override fun onNoInternetConnection() {
        mView.hideProgress()
        mView.noInternetConnection()
    }

    override fun onServiceNotAvailable() {
        mView.hideProgress()
        mView.serviceNotAvailable()
    }

    override fun onUserRetrieved(user: User) {
        mView.hideProgress()
        MessagingService.startService(mContext)
        mView.onUserRetrieved(user)
    }

    override fun onLogOut() {
        mView.hideProgress()
        MessagingService.stopService()
        mView.onLogOut()
    }
}
