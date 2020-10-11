package com.robert.android.lostpets.domain.interactors.impl

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.robert.android.lostpets.BuildConfig
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.SendMessageInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.domain.model.Message
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.network.converter.GsonSerializer
import ua.naiksoftware.stomp.StompClient

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz
 * SendMessageInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.SendMessageInteractor
 */
class SendMessageInteractorImpl(
    executor: Executor,
    mainThread: MainThread,
    stompClient: StompClient?,
    callback: SendMessageInteractor.Callback?,
    message: Message,
    user: User
) :
    AbstractInteractor(executor, mainThread), SendMessageInteractor {

    private val mStompClient: StompClient? = stompClient
    private val mCallback: SendMessageInteractor.Callback? = callback
    private val mMessage: Message = message
    private val mUser: User = user
    private val mGson: Gson = GsonSerializer.instance

    @SuppressLint("CheckResult")
    override fun run() {
        val defaultUserDestination = BuildConfig.LOST_PETS_USER_DESTINATION
        val userDestination = defaultUserDestination
                .replaceFirst("userEmail", mUser.email)
        mStompClient?.send(userDestination, mGson.toJson(mMessage))?.subscribe({
            mMainThread.post(Runnable {
                mCallback?.onMessageSent()
            })
        }, {
            mMainThread.post(Runnable {
                mCallback?.onMessageSentError(mMessage)
            })
        })
    }
}
