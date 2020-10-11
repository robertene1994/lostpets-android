package com.robert.android.lostpets.domain.interactors.impl

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.AdsInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.domain.utils.ConnectivityStatus
import com.robert.android.lostpets.network.service.AdService
import retrofit2.Call
import retrofit2.Response

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz AdsInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.AdsInteractor
 */
class AdsInteractorImpl(
    executor: Executor,
    mainThread: MainThread,
    callback: AdsInteractor.Callback,
    context: Context,
    service: AdService
) :
    AbstractInteractor(executor, mainThread), AdsInteractor {

    private val mCallback: AdsInteractor.Callback = callback
    private val mContext: Context = context
    private val mService: AdService = service

    override fun run() {
        mService.getAds().enqueue(object : retrofit2.Callback<List<Ad>> {
            override fun onResponse(call: Call<List<Ad>>, response: Response<List<Ad>>) {
                val ads = response.body()
                if (ads == null || ads.isEmpty()) {
                    mMainThread.post(Runnable { mCallback.onAdsEmpty() })
                } else {
                    mMainThread.post(Runnable { mCallback.onAdsRetrieved(ads) })
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
