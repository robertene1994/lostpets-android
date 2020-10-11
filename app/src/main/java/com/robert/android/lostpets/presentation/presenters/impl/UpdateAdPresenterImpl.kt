package com.robert.android.lostpets.presentation.presenters.impl

import android.content.Context
import android.net.Uri
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.ProcessImageInteractor
import com.robert.android.lostpets.domain.interactors.UpdateAdInteractor
import com.robert.android.lostpets.domain.interactors.impl.ProcessImageInteractorImpl
import com.robert.android.lostpets.domain.interactors.impl.UpdateAdInteractorImpl
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.network.service.AdService
import com.robert.android.lostpets.presentation.presenters.UpdateAdPresenter
import com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter
import java.io.File

/**
 * Clase que extiende la clase AbstractPresenter e implementa la interfaz UpdateAdPresenter.
 * Implementa las interfaces de los callbacks de los interactors ProcessImageInteractor y
 * UpdateAdInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter
 * @see com.robert.android.lostpets.presentation.presenters.UpdateAdPresenter
 * @see com.robert.android.lostpets.domain.interactors.ProcessImageInteractor.Callback
 * @see com.robert.android.lostpets.domain.interactors.UpdateAdInteractor.Callback
 */
class UpdateAdPresenterImpl(
    executor: Executor,
    mainThread: MainThread,
    view: UpdateAdPresenter.View,
    context: Context,
    service: AdService
) :
    AbstractPresenter(executor, mainThread), UpdateAdPresenter, ProcessImageInteractor.Callback,
        UpdateAdInteractor.Callback {

    private val mView: UpdateAdPresenter.View = view
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

    override fun processImage(uri: Uri) {
        ProcessImageInteractorImpl(mExecutor, mMainThread,
                this, mContext, uri).execute()
    }

    override fun updateAd(ad: Ad, image: File?) {
        mView.showProgress()
        UpdateAdInteractorImpl(mExecutor, mMainThread,
                this, mContext, mService, ad, image).execute()
    }

    override fun onProcessedImage(file: File) {
        mView.onProcessedImage(file)
    }

    override fun onUpdatedAd(adEdited: Boolean) {
        mView.hideProgress()
        mView.onAdUpdated(adEdited)
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
