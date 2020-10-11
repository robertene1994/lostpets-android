package com.robert.android.lostpets.presentation.presenters.impl

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.AdsInteractor
import com.robert.android.lostpets.domain.interactors.impl.AdsInteractorImpl
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.domain.model.types.AdStatus
import com.robert.android.lostpets.network.service.AdService
import com.robert.android.lostpets.presentation.presenters.AdsPresenter
import com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter

/**
 * Clase que extiende la clase AbstractPresenter e implementa la interfaz AdsPresenter.
 * Implementa la interfaz del callback del interactor AdsInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter
 * @see com.robert.android.lostpets.presentation.presenters.AdsPresenter
 * @see com.robert.android.lostpets.domain.interactors.AdsInteractor.Callback
 */
class AdsPresenterImpl(executor: Executor, mainThread: MainThread,
                       view: AdsPresenter.View, context: Context, service: AdService)
    : AbstractPresenter(executor, mainThread), AdsPresenter, AdsInteractor.Callback {


    private val mView: AdsPresenter.View = view
    private val mContext: Context = context
    private val mService: AdService = service

    override fun resume() {
        // no aplicable
    }

    override fun pause() {
        // no aplicable
    }

    override fun stop() {
        // no aplicable
    }

    override fun destroy() {
        // no aplicable
    }

    override fun getAds() {
        mView.showProgress()
        AdsInteractorImpl(mExecutor, mMainThread,
                this, mContext, mService).execute()
    }

    override fun onAdsRetrieved(ads: List<Ad>) {
        mView.hideProgress()
        mView.showAds(ads.filter { it.adStatus === AdStatus.ENABLED })
    }

    override fun onAdsEmpty() {
        mView.hideProgress()
        mView.onAdsEmpty()
    }

    override fun onNoInternetConnection() {
        mView.hideProgress()
        mView.noInternetConnection()
    }

    override fun onServiceNotAvailable() {
        mView.hideProgress()
        mView.serviceNotAvailable()
    }
}
