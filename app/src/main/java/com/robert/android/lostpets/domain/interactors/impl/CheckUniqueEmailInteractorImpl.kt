package com.robert.android.lostpets.domain.interactors.impl

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.CheckUniqueEmailInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.domain.utils.ConnectivityStatus
import com.robert.android.lostpets.network.service.UserService
import retrofit2.Call
import retrofit2.Response

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz
 * CheckUniqueEmailInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.CheckUniqueEmailInteractor
 */
class CheckUniqueEmailInteractorImpl(
    executor: Executor,
    mainThread: MainThread,
    callback: CheckUniqueEmailInteractor.Callback,
    context: Context,
    service: UserService,
    email: String
) :
    AbstractInteractor(executor, mainThread), CheckUniqueEmailInteractor {

    private val mCallback: CheckUniqueEmailInteractor.Callback = callback
    private val mContext: Context = context
    private val mService: UserService = service
    private val mEmail: String = email

    override fun run() {
        mService.checkUniqueEmail(mEmail).enqueue(object : retrofit2.Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val isUnique = response.body()
                if (isUnique != null)
                    mMainThread.post(Runnable { mCallback.onCheckUniqueEmail(isUnique) })
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
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
