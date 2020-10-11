package com.robert.android.lostpets.presentation.presenters.impl

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.UserChatsInteractor
import com.robert.android.lostpets.domain.interactors.impl.UserChatsInteractorImpl
import com.robert.android.lostpets.domain.model.Chat
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.network.service.ChatService
import com.robert.android.lostpets.presentation.presenters.UserChatsPresenter
import com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter

/**
 * Clase que extiende la clase AbstractPresenter e implementa la interfaz UserChatsPresenter.
 * Implementa la interfaz del callback del interactor UserChatsInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter
 * @see com.robert.android.lostpets.presentation.presenters.UserChatsPresenter
 * @see com.robert.android.lostpets.domain.interactors.UserChatsInteractor.Callback
 */
class UserChatsPresenterImpl(executor: Executor, mainThread: MainThread,
                             view: UserChatsPresenter.View, context: Context,
                             service: ChatService, user: User)
    : AbstractPresenter(executor, mainThread), UserChatsPresenter, UserChatsInteractor.Callback {

    private val mView: UserChatsPresenter.View = view
    private val mContext: Context = context
    private val mService: ChatService = service
    private val mUser: User = user

    override fun resume() {
        getUserChats()
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

    override fun getUserChats() {
        mView.showProgress()
        UserChatsInteractorImpl(mExecutor, mMainThread,
                this, mContext, mService, mUser).execute()
    }

    override fun onUserChatsRetrieved(chats: List<Chat>) {
        mView.hideProgress()
        mView.showUserChats(chats)
    }

    override fun onUserChatsEmpty() {
        mView.hideProgress()
        mView.onUserChatsEmpty()
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
