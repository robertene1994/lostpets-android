package com.robert.android.lostpets.domain.interactors.impl

import com.robert.android.lostpets.domain.executor.Executor
import com.robert.android.lostpets.domain.executor.MainThread
import com.robert.android.lostpets.domain.interactors.GetLanguageInteractor
import com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
import com.robert.android.lostpets.domain.repository.SettingsRepository
import java.util.Locale

/**
 * Clase que extiende la clase AbstractInteractor e implementa la interfaz GetLanguageInteractor.
 *
 * @author Robert Ene
 * @see com.robert.android.lostpets.domain.interactors.base.AbstractInteractor
 * @see com.robert.android.lostpets.domain.interactors.GetLanguageInteractor
 */
class GetLanguageInteractorImpl(
    executor: Executor,
    mainThread: MainThread,
    callback: GetLanguageInteractor.Callback,
    settingsRepository: SettingsRepository
) :
    AbstractInteractor(executor, mainThread), GetLanguageInteractor {

    private val mCallback: GetLanguageInteractor.Callback = callback
    private val mSettingsRepository: SettingsRepository = settingsRepository

    override fun run() {
        val language = checkDefault(mSettingsRepository.getLanguage())
        mMainThread.post(Runnable { mCallback.onLanguageRetrieved(language) })
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
