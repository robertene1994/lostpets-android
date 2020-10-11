package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.impl.GetLanguageInteractorImpl
import com.robert.android.lostpets.domain.repository.SettingsRepository
import com.robert.android.lostpets.utilTest.threading.TestMainThread
import java.util.Locale
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

/**
 * Clase test para la clase GetLanguageInteractorImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.impl.GetLanguageInteractorImpl
 */
@RunWith(MockitoJUnitRunner::class)
class GetLanguageInteractorTest {

    @Mock
    private lateinit var mExecutor: Executor
    @Mock
    private lateinit var mCallback: GetLanguageInteractor.Callback
    @Mock
    private lateinit var mSettingsRepository: SettingsRepository

    private lateinit var mMainThread: MainThread

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mMainThread = TestMainThread()
    }

    @Test
    @Throws(Exception::class)
    fun testGetLanguageNull() {
        val language: String? = null
        val languageDefaultValue: String = Locale.getDefault().language.toString()

        `when`(mSettingsRepository.getLanguage()).thenReturn(language)

        GetLanguageInteractorImpl(mExecutor, mMainThread, mCallback, mSettingsRepository).run()

        verify(mSettingsRepository).getLanguage()
        verify(mCallback).onLanguageRetrieved(languageDefaultValue)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun testGetLanguageEmpty() {
        val language = "   "
        val languageDefaultValue: String = Locale.getDefault().language.toString()

        `when`(mSettingsRepository.getLanguage()).thenReturn(language)

        GetLanguageInteractorImpl(mExecutor, mMainThread, mCallback, mSettingsRepository).run()

        verify(mSettingsRepository).getLanguage()
        verify(mCallback).onLanguageRetrieved(languageDefaultValue)
    }

    @Test
    @Throws(java.lang.Exception::class)
    fun testGetLanguage() {
        val language = "es_ES"

        `when`(mSettingsRepository.getLanguage()).thenReturn(language)

        GetLanguageInteractorImpl(mExecutor, mMainThread, mCallback, mSettingsRepository).run()

        verify(mSettingsRepository).getLanguage()
        verify(mCallback).onLanguageRetrieved(language)
    }
}
