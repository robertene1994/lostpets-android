package com.robert.android.lostpets.domain.interactors.impl

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.AddAdInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.domain.utils.ConnectivityStatus
import com.robert.android.lostpets.network.service.AdService
import java.io.File
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz AddAdInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.AddAdInteractor
 */
class AddAdInteractorImpl(
    executor: Executor,
    mainThread: MainThread,
    callback: AddAdInteractor.Callback,
    context: Context,
    service: AdService,
    ad: Ad,
    file: File
) :
    AbstractInteractor(executor, mainThread), AddAdInteractor {

    private val mCallback: AddAdInteractor.Callback = callback
    private val mContext: Context = context
    private val mService: AdService = service
    private val mAd: Ad = ad
    private val mFile: File = file

    override fun run() {
        val imageBody = RequestBody
                .create(MediaType.parse("multipart/form-data"), mFile)
        val imagePart = MultipartBody
                .Part.createFormData("image", "image", imageBody)

        mService.saveAd(mAd, imagePart).enqueue(object : retrofit2.Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                mMainThread.post(Runnable { mCallback.onAddedAd(response.body()!!) })
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
