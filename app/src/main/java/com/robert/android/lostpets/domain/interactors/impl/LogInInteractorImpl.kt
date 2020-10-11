package com.robert.android.lostpets.domain.interactors.impl

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.LogInInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.domain.model.AccountCredentials
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.Role
import com.robert.android.lostpets.domain.model.types.UserStatus
import com.robert.android.lostpets.domain.repository.SessionRepository
import com.robert.android.lostpets.domain.utils.ConnectivityStatus
import com.robert.android.lostpets.network.service.UserService
import retrofit2.Call
import retrofit2.Response

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz LogInInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.LogInInteractor
 */
class LogInInteractorImpl(
    executor: Executor,
    mainThread: MainThread,
    callback: LogInInteractor.Callback,
    context: Context,
    service: UserService,
    sessionRepository: SessionRepository,
    email: String,
    password: String
) :
    AbstractInteractor(executor, mainThread), LogInInteractor {

    private val mCallback: LogInInteractor.Callback = callback
    private val mContext: Context = context
    private val mService: UserService = service
    private val mSessionRepository: SessionRepository = sessionRepository
    private val mAccount: AccountCredentials = AccountCredentials(email, password)

    override fun run() {
        mService.logIn(mAccount).enqueue(object : retrofit2.Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val token = response.headers().get("Authorization")
                if (token == null) {
                    mMainThread.post(Runnable { mCallback.onFailureLogIn() })
                } else {
                    mSessionRepository.saveToken(token)
                    getUserDetails(mAccount.email)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                handleError(t)
            }
        })
    }

    private fun getUserDetails(email: String) {
        mService.getUserDetails(email).enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val user = response.body()
                if (user?.role != null && user.role != Role.USER) {
                    mMainThread.post(Runnable { mCallback.onRoleNotAllowedLogIn() })
                } else {
                    if (user != null) {
                        if (user.status === UserStatus.DISABLED) {
                            mMainThread.post(Runnable { mCallback.onUserDisabled() })
                        } else {
                            mSessionRepository.saveUser(user)
                            mMainThread.post(Runnable { mCallback.onSuccessLogIn(user) })
                        }
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                handleError(t)
            }
        })
    }

    private fun handleError(t: Throwable) {
        mMainThread.post(Runnable {
            if (!ConnectivityStatus.isInternetConnection(mContext)) {
                mCallback.onNoInternetConnection()
            } else if (t.message!!.contains("End of input")) {
                mCallback.onFailureLogIn()
            } else {
                mCallback.onServiceNotAvailable()
            }
        })
    }
}
