package com.robert.android.lostpets.domain.interactors.impl

import com.robert.android.lostpets.domain.interactors.GetLanguageSynchronousInteractor
import com.robert.android.lostpets.domain.repository.SettingsRepository
import java.util.Locale

/**
 * Clase que implementa la interfaz GetLanguageSynchronousInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.GetLanguageSynchronousInteractor
 */
class GetLanguageSynchronousInteractorImpl(settingsRepository: SettingsRepository) :
    GetLanguageSynchronousInteractor {

    private val mSettingsRepository: SettingsRepository = settingsRepository

    override fun getLanguage(): String {
        return checkDefault(mSettingsRepository.getLanguage())
    }

    private fun checkDefault(lang: String?): String {
        var language = lang
        if (language == null || language.trim().isEmpty()) {
            language = Locale.getDefault().language.toString()
            mSettingsRepository.saveLanguage(language)
        }
        return language
    }
}
