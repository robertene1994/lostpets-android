package com.robert.android.lostpets.domain.interactors.impl

import android.annotation.SuppressLint
import com.google.gson.Gson
import com.robert.android.lostpets.BuildConfig
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.HandleUserTopicConnectionInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.domain.model.Message
import com.robert.android.lostpets.domain.repository.SessionRepository
import com.robert.android.lostpets.network.converter.GsonSerializer
import ua.naiksoftware.stomp.StompClient

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz
 * HandleUserTopicConnectionInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.HandleStompConnectionInteractor
 */
class HandleUserTopicConnectionInteractorImpl(
    executor: Executor,
    mainThread: MainThread,
    stompClient: StompClient,
    sessionRepository: SessionRepository,
    callback: HandleUserTopicConnectionInteractor.Callback
) :
    AbstractInteractor(executor, mainThread), HandleUserTopicConnectionInteractor {

    private val mStompClient: StompClient = stompClient
    private val mSessionRepository: SessionRepository = sessionRepository
    private val mCallback: HandleUserTopicConnectionInteractor.Callback = callback
    private val mGson: Gson = GsonSerializer.instance

    @SuppressLint("CheckResult")
    override fun run() {
        val userTopic = BuildConfig.LOST_PETS_USER_TOPIC
                .replaceFirst("userEmail", mSessionRepository.getUser()!!.email)
        mStompClient.topic(userTopic).subscribe({
            val message = mGson.fromJson(it.payload, Message::class.java)
            mMainThread.post(Runnable {
                mCallback.onMessageReceived(message)
            })
        }, {
            mMainThread.post(Runnable {
                mCallback.onMessageReceivedError()
            })
        })
    }
}
