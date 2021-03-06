package com.robert.android.lostpets.presentation.presenters

import android.content.Context
import android.net.Uri
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.network.service.AdService
import com.robert.android.lostpets.presentation.presenters.impl.AddAdPresenterImpl
import com.robert.android.lostpets.utilTest.threading.TestMainThread
import java.io.File
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner

/**
 * Clase test para la clase AddAdPresenterImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.impl.AddAdPresenterImpl
 */
@RunWith(MockitoJUnitRunner::class)
class AddAdPresenterTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: AddAdPresenter.View
    @Mock
    private lateinit var mContext: Context
    @Mock
    private lateinit var mService: AdService
    @Mock
    private lateinit var mAd: Ad
    @Mock
    private lateinit var mUri: Uri
    @Mock
    private lateinit var mFile: File

    private lateinit var mMainThread: MainThread

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()
    }

    @Test
    @Throws(Exception::class)
    fun testProcessedImage() {
        val presenter = AddAdPresenterImpl(mExecutor, mMainThread, mCallback, mContext, mService)
        presenter.processImage(mUri)

        spy(presenter).pause()
        spy(presenter).resume()
        spy(presenter).onProcessedImage(mFile)
        spy(presenter).stop()
        spy(presenter).destroy()

        verify(mCallback).onProcessedImage(mFile)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testAdAddedSuccess() {
        val presenter = AddAdPresenterImpl(mExecutor, mMainThread, mCallback, mContext, mService)
        presenter.addAd(mAd, mFile)

        spy(presenter).onAddedAd(true)

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).onAdAdded(true)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testAdAddedFailure() {
        val presenter = AddAdPresenterImpl(mExecutor, mMainThread, mCallback, mContext, mService)
        presenter.addAd(mAd, mFile)

        spy(presenter).onAddedAd(false)

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).onAdAdded(false)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testNoInternetConnection() {
        val presenter = AddAdPresenterImpl(mExecutor, mMainThread, mCallback, mContext, mService)
        presenter.processImage(mUri)

        spy(presenter).onNoInternetConnection()

        verify(mCallback).hideProgress()
        verify(mCallback).noInternetConnection()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testServiceNotAvailable() {
        val presenter = AddAdPresenterImpl(mExecutor, mMainThread, mCallback, mContext, mService)
        presenter.processImage(mUri)

        spy(presenter).onServiceNotAvailable()

        verify(mCallback).hideProgress()
        verify(mCallback).serviceNotAvailable()
        verifyNoMoreInteractions(mCallback)
    }
}
