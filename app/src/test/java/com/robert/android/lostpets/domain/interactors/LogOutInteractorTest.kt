package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.impl.LogOutInteractorImpl
import com.robert.android.lostpets.domain.repository.SessionRepository
import com.robert.android.lostpets.utilTest.threading.TestMainThread
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner

/**
 * Clase test para la clase LogOutInteractorImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.impl.LogOutInteractorImpl
 */
@RunWith(MockitoJUnitRunner::class)
class LogOutInteractorTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: LogOutInteractor.Callback
    @Mock
    private lateinit var mSessionRepository: SessionRepository

    private lateinit var mMainThread: MainThread

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()
    }

    @Test
    @Throws(Exception::class)
    fun testLogOut() {
        LogOutInteractorImpl(mExecutor, mMainThread, mCallback, mSessionRepository).run()

        verify(mSessionRepository).deleteUser()
        verify(mSessionRepository).deleteToken()
        verify(mCallback).onLogOut()
        verifyNoMoreInteractions(mSessionRepository, mCallback)
    }
}
