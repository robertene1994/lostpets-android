package com.robert.android.lostpets.domain.interactors.impl

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.UserChatsInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.domain.model.Chat
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.utils.ConnectivityStatus
import com.robert.android.lostpets.network.service.ChatService
import retrofit2.Call
import retrofit2.Response

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz UserChatsInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.UserChatsInteractor
 */
class UserChatsInteractorImpl(executor: Executor, mainThread: MainThread,
                              callback: UserChatsInteractor.Callback,
                              context: Context, service: ChatService, user: User)
    : AbstractInteractor(executor, mainThread), UserChatsInteractor {

    private val mCallback: UserChatsInteractor.Callback = callback
    private val mContext: Context = context
    private val mService: ChatService = service
    private val mUser: User = user

    override fun run() {
        mService.getUserChats(mUser.id!!).enqueue(object : retrofit2.Callback<List<Chat>> {
            override fun onResponse(call: Call<List<Chat>>, response: Response<List<Chat>>) {
                val chats = response.body()
                if (chats == null || chats.isEmpty()) {
                    mMainThread.post(Runnable { mCallback.onUserChatsEmpty() })
                } else {
                    mMainThread.post(Runnable { mCallback.onUserChatsRetrieved(chats) })
                }
            }

            override fun onFailure(call: Call<List<Chat>>, t: Throwable) {
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
