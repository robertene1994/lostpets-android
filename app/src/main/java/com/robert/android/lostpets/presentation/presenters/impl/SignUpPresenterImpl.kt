package com.robert.android.lostpets.presentation.presenters.impl

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.CheckUniqueEmailInteractor
import com.robert.android.lostpets.domain.interactors.SignUpInteractor
import com.robert.android.lostpets.domain.interactors.impl.CheckUniqueEmailInteractorImpl
import com.robert.android.lostpets.domain.interactors.impl.SignUpInteractorImpl
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.network.service.UserService
import com.robert.android.lostpets.presentation.presenters.SignUpPresenter
import com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter

/**
 * Clase que extiende la clase AbstractPresenter e implementa la interfaz SignUpPresenter.
 * Implementa las interfaces de los callbacks de los interactors SignUpInteractor y
 * CheckUniqueEmailInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter
 * @see com.robert.android.lostpets.presentation.presenters.SignUpPresenter
 * @see com.robert.android.lostpets.domain.interactors.CheckUniqueEmailInteractor.Callback
 * @see com.robert.android.lostpets.domain.interactors.SignUpInteractor.Callback
 */
class SignUpPresenterImpl(executor: Executor, mainThread: MainThread, view: SignUpPresenter.View,
                          context: Context, service: UserService)
    : AbstractPresenter(executor, mainThread), SignUpPresenter,
        CheckUniqueEmailInteractor.Callback, SignUpInteractor.Callback {

    private val mView: SignUpPresenter.View = view
    private val mContext: Context = context
    private val mService: UserService = service

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

    override fun checkUniqueEmail(email: String) {
        mView.showProgress()
        CheckUniqueEmailInteractorImpl(mExecutor, mMainThread,
                this, mContext, mService, email).execute()
    }

    override fun signUp(user: User) {
        mView.showProgress()
        SignUpInteractorImpl(mExecutor, mMainThread, this,
                mContext, mService, user).execute()
    }

    override fun onCheckUniqueEmail(isUnique: Boolean) {
        mView.hideProgress()
        mView.onCheckUniqueEmail(isUnique)
    }

    override fun onSignUp(user: User) {
        mView.hideProgress()
        mView.onSignUp(user)
    }

    override fun onNoInternetConnection() {
        mView.hideProgress()
        mView.noInternetConnection()
    }

    override fun onServiceNotAvailable() {
        mView.hideProgress()
        mView.serviceNotAvailable()
    }
}
