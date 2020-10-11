package com.robert.android.lostpets.presentation.presenters.impl

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.LogInInteractor
import com.robert.android.lostpets.domain.interactors.LogOutInteractor
import com.robert.android.lostpets.domain.interactors.UserSessionInteractor
import com.robert.android.lostpets.domain.interactors.impl.LogInInteractorImpl
import com.robert.android.lostpets.domain.interactors.impl.LogOutInteractorImpl
import com.robert.android.lostpets.domain.interactors.impl.UserSessionInteractorImpl
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.repository.SessionRepository
import com.robert.android.lostpets.network.message.MessagingService
import com.robert.android.lostpets.network.service.UserService
import com.robert.android.lostpets.presentation.presenters.LogInPresenter
import com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter

/**
 * Clase que extiende la clase AbstractPresenter e implementa la interfaz LogInPresenter.
 * Implementa las interfaces de los callbacks de los interactors LogInInteractor,
 * UserSessionInteractor, LogOutInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter
 * @see com.robert.android.lostpets.presentation.presenters.LogInPresenter
 * @see com.robert.android.lostpets.domain.interactors.LogInInteractor.Callback
 * @see com.robert.android.lostpets.domain.interactors.UserSessionInteractor.Callback
 * @see com.robert.android.lostpets.domain.interactors.LogOutInteractor.Callback
 */
class LogInPresenterImpl(
    executor: Executor,
    mainThread: MainThread,
    view: LogInPresenter.View,
    context: Context,
    service: UserService,
    sessionRepository: SessionRepository
) :
    AbstractPresenter(executor, mainThread), LogInPresenter, LogInInteractor.Callback,
        UserSessionInteractor.Callback, LogOutInteractor.Callback {

    private val mView: LogInPresenter.View = view
    private val mContext: Context = context
    private val mService: UserService = service
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

    override fun checkUserSession() {
        mView.showProgress()
        UserSessionInteractorImpl(mExecutor, mMainThread, this,
                mContext, mService, mSessionRepository).execute()
    }

    override fun logIn(email: String, password: String) {
        mView.showProgress()
        LogInInteractorImpl(mExecutor, mMainThread, this, mContext,
                mService, mSessionRepository, email, password).execute()
    }

    override fun onSuccessLogIn(user: User) {
        mView.hideProgress()
        mView.onSuccessLogIn(user)
    }

    override fun onFailureLogIn() {
        mView.hideProgress()
        mView.onFailureLogIn()
    }

    override fun onRoleNotAllowedLogIn() {
        mView.hideProgress()
        mView.onRoleNotAllowedLogIn()
    }

    override fun onLogOut() {
        mView.hideProgress()
        MessagingService.stopService()
    }

    override fun onNoInternetConnection() {
        mView.hideProgress()
        mView.noInternetConnection()
    }

    override fun onServiceNotAvailable() {
        mView.hideProgress()
        mView.serviceNotAvailable()
    }

    override fun onUserLoggedIn() {
        mView.onUserLoggedIn()
    }

    override fun onUserNotLoggedIn() {
        mView.hideProgress()
    }

    override fun onUserSessionExpired() {
        mView.hideProgress()
        mView.onUserSessionExpired()
    }

    override fun onUserDisabled() {
        LogOutInteractorImpl(mExecutor, mMainThread,
                this, mSessionRepository).execute()
        mView.onUserDisabled()
    }
}
