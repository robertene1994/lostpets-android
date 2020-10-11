package com.robert.android.lostpets.domain.interactors.impl

import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.SaveSettingsInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.domain.repository.SettingsRepository

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz SaveSettingsInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.SaveSettingsInteractor
 */
class SaveLanguageInteractorImpl(
    executor: Executor,
    mainThread: MainThread,
    callback: SaveSettingsInteractor.Callback,
    settingsRepository: SettingsRepository,
    language: String
) :
    AbstractInteractor(executor, mainThread), SaveSettingsInteractor {

    private val mCallback: SaveSettingsInteractor.Callback = callback
    private val mSettingsRepository: SettingsRepository = settingsRepository
    private val mLanguage: String = language

    override fun run() {
        mSettingsRepository.saveLanguage(mLanguage)
        mMainThread.post(Runnable { mCallback.onSavedSettings() })
    }
}
