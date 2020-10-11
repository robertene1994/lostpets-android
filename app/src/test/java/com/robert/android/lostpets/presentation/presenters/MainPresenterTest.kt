package com.robert.android.lostpets.presentation.presenters

import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.model.User
import com.robert.android.lostpets.domain.repository.SessionRepository
import com.robert.android.lostpets.presentation.presenters.impl.MainPresenterImpl
import com.robert.android.lostpets.utilTest.threading.TestMainThread
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Clase test para la clase MainPresenterImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.impl.MainPresenterImpl
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class MainPresenterTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: MainPresenter.View
    @Mock
    private lateinit var mSessionRepository: SessionRepository
    @Mock
    private lateinit var mUser: User

    private lateinit var mMainThread: MainThread

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mMainThread = TestMainThread()
    }

    @Test
    @Throws(Exception::class)
    fun testUserRetrieved() {
        val presenter = MainPresenterImpl(mExecutor, mMainThread, mCallback,
                RuntimeEnvironment.application,mSessionRepository)
        presenter.getUser()

        spy(presenter).pause()
        spy(presenter).resume()
        spy(presenter).onUserRetrieved(mUser)
        spy(presenter).stop()
        spy(presenter).destroy()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).onUserRetrieved(mUser)
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testLogOut() {
        val presenter = MainPresenterImpl(mExecutor, mMainThread, mCallback,
                RuntimeEnvironment.application,mSessionRepository)
        presenter.logOut()

        spy(presenter).onLogOut()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).onLogOut()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testNoInternetConnection() {
        val presenter = MainPresenterImpl(mExecutor, mMainThread, mCallback,
                RuntimeEnvironment.application,mSessionRepository)
        presenter.getUser()

        spy(presenter).onNoInternetConnection()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).noInternetConnection()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testServiceNotAvailable() {
        val presenter = MainPresenterImpl(mExecutor, mMainThread, mCallback,
                RuntimeEnvironment.application,mSessionRepository)
        presenter.getUser()

        spy(presenter).onServiceNotAvailable()

        verify(mCallback).showProgress()
        verify(mCallback).hideProgress()
        verify(mCallback).serviceNotAvailable()
        verifyNoMoreInteractions(mCallback)
    }
}
