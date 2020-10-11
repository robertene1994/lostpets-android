package com.robert.android.lostpets.domain.interactors.impl

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.ChatDetailInteractor
import com.robert.android.lostpets.domain.interactors.UserChatsInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.domain.model.Chat
import com.robert.android.lostpets.domain.model.Message
import com.robert.android.lostpets.domain.utils.ConnectivityStatus
import com.robert.android.lostpets.network.service.MessageService
import retrofit2.Call
import retrofit2.Response

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz ChatDetailInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.ChatDetailInteractor
 */
class ChatDetailInteractorImpl(executor: Executor, mainThread: MainThread,
                               callback: ChatDetailInteractor.Callback,
                               context: Context, service: MessageService, chat: Chat)
    : AbstractInteractor(executor, mainThread), UserChatsInteractor {

    private val mCallback: ChatDetailInteractor.Callback = callback
    private val mContext: Context = context
    private val mService: MessageService = service
    private val mChat: Chat = chat

    override fun run() {
        mService.getChatMessagesAndMarkAsRead(mChat.code, mChat.fromUser.email).enqueue(object :
                retrofit2.Callback<List<Message>> {
            override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
                val messages = response.body()
                if (messages == null || messages.isEmpty()) {
                    mMainThread.post(Runnable { mCallback.onChatMessagesEmpty() })
                } else {
                    mMainThread.post(Runnable { mCallback.onChatMessagesRetrieved(messages) })
                }
            }

            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                handleError()
            }
        })
    }

    private fun handleError() {
        mMainThread.post(Runnable {
            if (!ConnectivityStatus.isInternetConnection(mContext)) {
                mCallback.onNoInternetConnection()
            } else {
                mCallback.onServiceNotAvailable()
            }
        })
    }
}
