package com.robert.android.lostpets.domain.interactors

import android.net.Uri
import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.impl.ProcessImageInteractorImpl
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
import java.io.File

/**
 * Clase test para la clase ProcessImageInteractorImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.impl.ProcessImageInteractorImpl
 */
@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class ProcessImageInteractorTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: ProcessImageInteractor.Callback

    private lateinit var mMainThread: MainThread

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mMainThread = TestMainThread()
    }

    @Test
    @Throws(Exception::class)
    fun testProcessedImage() {
        val file = File(spy(RuntimeEnvironment.application).cacheDir, "petImage")

        ProcessImageInteractorImpl(mExecutor, mMainThread, mCallback,
                RuntimeEnvironment.application, mock(Uri::class.java)).run()

        verify(mCallback).onProcessedImage(file)
        verifyNoMoreInteractions(mCallback)
    }
}
