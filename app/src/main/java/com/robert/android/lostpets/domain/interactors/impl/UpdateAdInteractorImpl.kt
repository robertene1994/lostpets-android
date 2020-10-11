package com.robert.android.lostpets.domain.interactors.impl

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.UpdateAdInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.domain.utils.ConnectivityStatus
import com.robert.android.lostpets.network.service.AdService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz UpdateAdInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.UpdateAdInteractor
 */
class UpdateAdInteractorImpl(executor: Executor, mainThread: MainThread,
                             callback: UpdateAdInteractor.Callback,
                             context: Context, service: AdService, ad: Ad, file: File?)
    : AbstractInteractor(executor, mainThread), UpdateAdInteractor {

    private val mCallback: UpdateAdInteractor.Callback = callback
    private val mContext: Context = context
    private val mService: AdService = service
    private val mAd: Ad = ad
    private val mFile: File? = file

    override fun run() {
        var imagePart: MultipartBody.Part? = null
        if (mFile != null) {
            val imageBody = RequestBody
                    .create(MediaType.parse("multipart/form-data"), mFile)
            imagePart = MultipartBody
                    .Part.createFormData("image", "image", imageBody)
        }

        mService.updateAd(mAd, imagePart).enqueue(object : retrofit2.Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                mMainThread.post(Runnable { mCallback.onUpdatedAd(response.body()!!) })
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
