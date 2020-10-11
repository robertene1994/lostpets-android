package com.robert.android.lostpets.domain.interactors.impl

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.SignUpInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.utils.ConnectivityStatus
import com.robert.android.lostpets.network.service.UserService
import retrofit2.Call
import retrofit2.Response

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz SignUpInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.SignUpInteractor
 */
class SignUpInteractorImpl(executor: Executor, mainThread: MainThread,
                           callback: SignUpInteractor.Callback,
                           context: Context, service: UserService, user: User)
    : AbstractInteractor(executor, mainThread), SignUpInteractor {

    private val mCallback: SignUpInteractor.Callback = callback
    private val mContext: Context = context
    private val mService: UserService = service
    private val mUser: User = user

    override fun run() {
        mService.signUp(mUser).enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val user = response.body()
                if (user != null)
                    mMainThread.post(Runnable { mCallback.onSignUp(user) })
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
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
