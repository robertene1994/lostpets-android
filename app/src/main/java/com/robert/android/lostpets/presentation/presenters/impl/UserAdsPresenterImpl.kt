package com.robert.android.lostpets.presentation.presenters.impl

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.UserAdsInteractor
import com.robert.android.lostpets.domain.interactors.impl.UserAdsInteractorImpl
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.model.types.AdStatus
import com.robert.android.lostpets.network.service.AdService
import com.robert.android.lostpets.presentation.presenters.UserAdsPresenter
import com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter

/**
 * Clase que extiende la clase AbstractPresenter e implementa la interfaz UserAdsPresenter.
 * Implementa la interfaz del callback del interactor UserAdsInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter
 * @see com.robert.android.lostpets.presentation.presenters.UserAdsPresenter
 * @see com.robert.android.lostpets.domain.interactors.UserAdsInteractor.Callback
 */
class UserAdsPresenterImpl(executor: Executor, mainThread: MainThread,
                           view: UserAdsPresenter.View, context: Context,
                           service: AdService, user: User)
    : AbstractPresenter(executor, mainThread), UserAdsPresenter, UserAdsInteractor.Callback {

    private val mView: UserAdsPresenter.View = view
    private val mContext: Context = context
    private val mService: AdService = service
    private val mUser: User = user

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

    override fun getUserAds() {
        mView.showProgress()
        UserAdsInteractorImpl(mExecutor, mMainThread,this,
                mContext, mService, mUser).execute()
    }

    override fun onUserAdsRetrieved(ads: List<Ad>) {
        mView.hideProgress()
        mView.showUserAds(ads)
        if (!ads.none { it.adStatus === AdStatus.DISABLED }) {
            mView.onUserDisabledAds()
        }
    }

    override fun onUserAdsEmpty() {
        mView.hideProgress()
        mView.onUserAdsEmpty()
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
