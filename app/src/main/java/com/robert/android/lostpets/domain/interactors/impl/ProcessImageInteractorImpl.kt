package com.robert.android.lostpets.domain.interactors.impl

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.ProcessImageInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz ProcessImageInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.ProcessImageInteractor
 */
class ProcessImageInteractorImpl(executor: Executor, mainThread: MainThread,
                                 callback: ProcessImageInteractor.Callback,
                                 context: Context, uri: Uri)
    : AbstractInteractor(executor, mainThread), ProcessImageInteractor {

    companion object {
        private const val PET_IMAGE_FILE_NAME = "petImage"
    }

    private val mCallback: ProcessImageInteractor.Callback = callback
    private val mContext: Context = context
    private val mUri: Uri = uri
    private lateinit var mFile: File

    override fun run() {
        val bitmap = MediaStore.Images.Media.getBitmap(mContext.contentResolver, mUri)
        mFile = bitmapToFile(bitmap)
        mMainThread.post(Runnable { mCallback.onProcessedImage(mFile) })
    }

    private fun bitmapToFile(bitmap: Bitmap): File {
        val file = File(mContext.cacheDir, PET_IMAGE_FILE_NAME)
        file.createNewFile()

        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bos)
        val bitmapdata = bos.toByteArray()

        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        return file
    }
}
