package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.impl.HandleStompConnectionInteractorImpl
import com.robert.android.lostpets.utilTest.threading.TestMainThread
import io.reactivex.BackpressureStrategy
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.MockitoJUnitRunner
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

/**
 * Clase test para la clase HandleStompConnectionInteractorImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.impl.HandleStompConnectionInteractorImpl
 */
@RunWith(MockitoJUnitRunner::class)
class HandleStompConnectionInteractorTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: HandleStompConnectionInteractor.Callback
    @Mock
    private lateinit var mStompClient: StompClient

    private lateinit var mLifecyclePublishSubject: PublishSubject<LifecycleEvent>
    private lateinit var mMainThread: MainThread

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()

        mLifecyclePublishSubject = PublishSubject.create()
        `when`(mStompClient.lifecycle())
                .thenReturn(mLifecyclePublishSubject.toFlowable(BackpressureStrategy.BUFFER))
    }

    @Test
    @Throws(Exception::class)
    fun testConnectionOpened() {
        HandleStompConnectionInteractorImpl(mExecutor, mMainThread, mStompClient, mCallback).run()

        mLifecyclePublishSubject.onNext(LifecycleEvent(LifecycleEvent.Type.OPENED))

        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testConnectionError() {
        HandleStompConnectionInteractorImpl(mExecutor, mMainThread, mStompClient, mCallback).run()

        mLifecyclePublishSubject.onNext(LifecycleEvent(LifecycleEvent.Type.ERROR))

        verify(mCallback).onConnectionError()
        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testConnectionClosed() {
        HandleStompConnectionInteractorImpl(mExecutor, mMainThread, mStompClient, mCallback).run()

        mLifecyclePublishSubject.onNext(LifecycleEvent(LifecycleEvent.Type.CLOSED))

        verifyNoMoreInteractions(mCallback)
    }

    @Test
    @Throws(Exception::class)
    fun testFailedServerHeartbeat() {
        HandleStompConnectionInteractorImpl(mExecutor, mMainThread, mStompClient, mCallback).run()

        mLifecyclePublishSubject.onNext(LifecycleEvent(LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT))

        verify(mCallback).onFailedServerHeartbeat()
        verifyNoMoreInteractions(mCallback)
    }
}
