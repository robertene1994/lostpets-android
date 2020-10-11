package com.robert.android.lostpets.presentation.presenters.impl

import android.content.Context
import android.net.Uri
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.AddAdInteractor
import com.robert.android.lostpets.domain.interactors.ProcessImageInteractor
import com.robert.android.lostpets.domain.interactors.impl.AddAdInteractorImpl
import com.robert.android.lostpets.domain.interactors.impl.ProcessImageInteractorImpl
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.network.service.AdService
import com.robert.android.lostpets.presentation.presenters.AddAdPresenter
import com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter
import java.io.File

/**
 * Clase que extiende la clase AbstractPresenter e implementa la interfaz AddAdPresenter.
 * Implementa la interfaz del callback del interactor ProcessImageInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.base.AbstractPresenter
 * @see com.robert.android.lostpets.presentation.presenters.AddAdPresenter
 * @see com.robert.android.lostpets.domain.interactors.ProcessImageInteractor.Callback
 */
class AddAdPresenterImpl(
    executor: Executor,
    mainThread: MainThread,
    view: AddAdPresenter.View,
    context: Context,
    service: AdService
) :
    AbstractPresenter(executor, mainThread), AddAdPresenter, ProcessImageInteractor.Callback,
        AddAdInteractor.Callback {

    private val mView: AddAdPresenter.View = view
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

    override fun addAd(ad: Ad, image: File) {
        mView.showProgress()
        AddAdInteractorImpl(mExecutor, mMainThread,
                this, mContext, mService, ad, image).execute()
    }

    override fun onProcessedImage(file: File) {
        mView.onProcessedImage(file)
    }

    override fun onAddedAd(adAdded: Boolean) {
        mView.hideProgress()
        mView.onAdAdded(adAdded)
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
