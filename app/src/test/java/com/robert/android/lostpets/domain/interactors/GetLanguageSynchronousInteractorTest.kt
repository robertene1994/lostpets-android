package com.robert.android.lostpets.domain.interactors

import com.robert.android.lostpets.domain.interactors.impl.GetLanguageSynchronousInteractorImpl
import com.robert.android.lostpets.domain.repository.SettingsRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

/**
 * Clase test para la clase GetLanguageSynchronousInteractorImpl.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.impl.GetLanguageSynchronousInteractorImpl
 */
@RunWith(MockitoJUnitRunner::class)
class GetLanguageSynchronousInteractorTest {

    @Mock
    private lateinit var mSettingsRepository: SettingsRepository

    @Test
    @Throws(Exception::class)
    fun testGetLanguageNull() {
        val language: String? = null
        val languageDefaultValue: String = Locale.getDefault().language.toString()

        `when`(mSettingsRepository.getLanguage()).thenReturn(language)

        val interactor = GetLanguageSynchronousInteractorImpl(mSettingsRepository)

        assertEquals(languageDefaultValue, interactor.getLanguage())
        verify(mSettingsRepository).getLanguage()
    }

    @Test
    @Throws(Exception::class)
    fun testGetLanguageEmpty() {
        val language = "       "
        val languageDefaultValue: String = Locale.getDefault().language.toString()

        `when`(mSettingsRepository.getLanguage()).thenReturn(language)

        val interactor = GetLanguageSynchronousInteractorImpl(mSettingsRepository)

        assertEquals(languageDefaultValue, interactor.getLanguage())
        verify(mSettingsRepository).getLanguage()
    }

    @Test
    @Throws(Exception::class)
    fun testGetLanguage() {
        val language = "es_ES"

        `when`(mSettingsRepository.getLanguage()).thenReturn(language)

        val interactor = GetLanguageSynchronousInteractorImpl(mSettingsRepository)

        assertEquals(language, interactor.getLanguage())
        verify(mSettingsRepository).getLanguage()
    }
}
