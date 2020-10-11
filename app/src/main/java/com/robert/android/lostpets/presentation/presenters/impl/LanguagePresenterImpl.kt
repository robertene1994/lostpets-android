package com.robert.android.lostpets.presentation.presenters.impl

import com.robert.android.lostpets.domain.interactors.impl.GetLanguageSynchronousInteractorImpl
import com.robert.android.lostpets.domain.repository.SettingsRepository
import com.robert.android.lostpets.presentation.presenters.LanguagePresenter

/**
 * Clase que implementa la interfaz LanguagePresenter.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.presentation.presenters.LanguagePresenter
 */
class LanguagePresenterImpl(settingsRepository: SettingsRepository) : LanguagePresenter {

    private val mSettingsRepository: SettingsRepository = settingsRepository

    override fun getLanguage(): String {
        return GetLanguageSynchronousInteractorImpl(mSettingsRepository).getLanguage()
    }
}
