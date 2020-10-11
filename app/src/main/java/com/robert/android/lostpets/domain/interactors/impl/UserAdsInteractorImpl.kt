package com.robert.android.lostpets.domain.interactors.impl

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.UserAdsInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.utils.ConnectivityStatus
import com.robert.android.lostpets.network.service.AdService
import retrofit2.Call
import retrofit2.Response

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz UserAdsInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.UserAdsInteractor
 */
class UserAdsInteractorImpl(executor: Executor, mainThread: MainThread,
                            callback: UserAdsInteractor.Callback,
                            context: Context, service: AdService, user: User)
    : AbstractInteractor(executor, mainThread), UserAdsInteractor {

    private val mCallback: UserAdsInteractor.Callback = callback
    private val mContext: Context = context
    private val mService: AdService = service
    private val mUser: User = user

    override fun run() {
        mService.getUserAds(mUser.id!!).enqueue(object : retrofit2.Callback<List<Ad>> {
            override fun onResponse(call: Call<List<Ad>>, response: Response<List<Ad>>) {
                val ads = response.body()
                if (ads == null || ads.isEmpty()) {
                    mMainThread.post(Runnable { mCallback.onUserAdsEmpty() })
                } else {
                    mMainThread.post(Runnable { mCallback.onUserAdsRetrieved(ads) })
                }
            }

            override fun onFailure(call: Call<List<Ad>>, t: Throwable) {
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
