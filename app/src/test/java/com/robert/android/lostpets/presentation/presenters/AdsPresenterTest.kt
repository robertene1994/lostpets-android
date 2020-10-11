package com.robert.android.lostpets.presentation.presenters

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.network.service.AdService
import com.robert.android.lostpets.presentation.presenters.impl.AdsPresenterImpl
import com.robert.android.lostpets.utilTest.threading.TestMainThread
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

/**
 * Clase test para la clase AdsPresenterImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.impl.AdsPresenterImpl
 */
@RunWith(MockitoJUnitRunner::class)
class AdsPresenterTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: AdsPresenter.View
    @Mock
    private lateinit var mContext: Context
    @Mock
    private lateinit var mService: AdService

    private lateinit var mMainThread: MainThread
    private lateinit var mAds: List<Ad>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()
    }

    @Test
    @Throws(Exception::class)
    fun testAdsRetrieved() {
        mAds = listOf()

        val presenter = AdsPresenterImpl(mExecutor, mMainThread, mCallback, mContext, mService)
        presenter.getAds()

        spy(presenter).pause()
        spy(presenter).resume()
        spy(presenter).onAdsRetrieved(mAds)
        spy(presenter).stop()
        spy(presenter).destroy()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).showAds(mAds)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testAdsEmpty() {
        val presenter = AdsPresenterImpl(mExecutor, mMainThread, mCallback, mContext, mService)
        presenter.getAds()

        spy(presenter).onAdsEmpty()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).onAdsEmpty()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testNoInternetConnection() {
        val presenter = AdsPresenterImpl(mExecutor, mMainThread, mCallback, mContext, mService)
        presenter.getAds()

        spy(presenter).onNoInternetConnection()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).noInternetConnection()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testServiceNotAvailable() {
        val presenter = AdsPresenterImpl(mExecutor, mMainThread, mCallback, mContext, mService)
        presenter.getAds()

        spy(presenter).onServiceNotAvailable()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).serviceNotAvailable()
        verifyNoMoreInteractions(mCallback)
    }
}
