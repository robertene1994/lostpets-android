package com.robert.android.lostpets.domain.interactors.impl

import android.annotation.SuppressLint
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.HandleStompConnectionInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.network.message.MessagingService
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz
 * HandleStompConnectionInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.HandleStompConnectionInteractor
 */
class HandleStompConnectionInteractorImpl(executor: Executor, mainThread: MainThread,
                                          stompClient: StompClient,
                                          callback: HandleStompConnectionInteractor.Callback)
    : AbstractInteractor(executor, mainThread), HandleStompConnectionInteractor {

    private val mStompClient: StompClient = stompClient
    private var mCallback: HandleStompConnectionInteractor.Callback = callback

    @SuppressLint("CheckResult")
    override fun run() {
        mStompClient.lifecycle().subscribe { lifecycleEvent ->
            when (lifecycleEvent?.type) {
                LifecycleEvent.Type.OPENED -> { }
                LifecycleEvent.Type.ERROR ->
                    mMainThread.post(Runnable { mCallback.onConnectionError() })
                LifecycleEvent.Type.CLOSED ->
                    mMainThread.post(Runnable { MessagingService.restartService() })
                LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT ->
                    mMainThread.post(Runnable { mCallback.onFailedServerHeartbeat() })
            }
        }
    }
}
