package com.robert.android.lostpets.presentation.presenters

import android.content.Context
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.model.Ad
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.network.service.AdService
import com.robert.android.lostpets.presentation.presenters.impl.UserAdsPresenterImpl
import com.robert.android.lostpets.utilTest.threading.TestMainThread
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner

/**
 * Clase test para la clase UserAdsPresenterImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.impl.UserAdsPresenterImpl
 */
@RunWith(MockitoJUnitRunner::class)
class UserAdsPresenterTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: UserAdsPresenter.View
    @Mock
    private lateinit var mContext: Context
    @Mock
    private lateinit var mService: AdService
    @Mock
    private lateinit var mUser: User

    private lateinit var mMainThread: MainThread
    private lateinit var mAds: List<Ad>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()

        mAds = listOf()
    }

    @Test
    @Throws(Exception::class)
    fun testUserAdsRetrieved() {
        val presenter = UserAdsPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mUser)
        presenter.getUserAds()

        spy(presenter).pause()
        spy(presenter).resume()
        spy(presenter).onUserAdsRetrieved(mAds)
        spy(presenter).stop()
        spy(presenter).destroy()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).showUserAds(mAds)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testUserAdsEmpty() {
        val presenter = UserAdsPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mUser)
        presenter.getUserAds()

        spy(presenter).onUserAdsEmpty()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).onUserAdsEmpty()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testNoInternetConnection() {
        val presenter = UserAdsPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mUser)
        presenter.getUserAds()

        spy(presenter).onNoInternetConnection()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).noInternetConnection()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testServiceNotAvailable() {
        val presenter = UserAdsPresenterImpl(mExecutor, mMainThread,
                mCallback, mContext, mService, mUser)
        presenter.getUserAds()

        spy(presenter).onServiceNotAvailable()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).serviceNotAvailable()
        verifyNoMoreInteractions(mCallback)
    }
}
