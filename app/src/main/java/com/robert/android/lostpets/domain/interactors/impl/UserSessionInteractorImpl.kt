package com.robert.android.lostpets.domain.interactors.impl

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.UserSessionInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.domain.model.types.UserStatus
import com.robert.android.lostpets.domain.repository.SessionRepository
import com.robert.android.lostpets.domain.utils.ConnectivityStatus
import com.robert.android.lostpets.domain.utils.Jwt
import com.robert.android.lostpets.network.service.UserService
import retrofit2.Call
import retrofit2.Response

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz UserSessionInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.UserSessionInteractor
 */
class UserSessionInteractorImpl(executor: Executor, mainThread: MainThread,
                                callback: UserSessionInteractor.Callback,
                                context: Context, service: UserService,
                                sessionRepository: SessionRepository)
    : AbstractInteractor(executor, mainThread), UserSessionInteractor {

    private val mCallback: UserSessionInteractor.Callback = callback
    private val mContext: Context = context
    private val mService: UserService = service
    private val mSessionRepository: SessionRepository = sessionRepository

    override fun run() {
        val user = mSessionRepository.getUser()
        val token = mSessionRepository.getToken()

        if (user == null || token == null) {
            mMainThread.post(Runnable { mCallback.onUserNotLoggedIn() })
        } else if (tokenIsExpired(token)) {
            mMainThread.post(Runnable { mCallback.onUserSessionExpired() })
        } else {
            mService.checkUserStatus(user.email).enqueue(object : retrofit2.Callback<UserStatus> {
                override fun onResponse(call: Call<UserStatus>, response: Response<UserStatus>) {
                    if (response.body()!! == UserStatus.DISABLED) {
                        mMainThread.post(Runnable { mCallback.onUserDisabled() })
                    } else {
                        mMainThread.post(Runnable { mCallback.onUserLoggedIn() })
                    }
                }

                override fun onFailure(call: Call<UserStatus>, t: Throwable) {
                    handleError()
                }
            })
        }
    }

    private fun tokenIsExpired(token: String?): Boolean {
        if (token!!.trim().isEmpty())
            return true
        return Jwt(token).isExpired()
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
