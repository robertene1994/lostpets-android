package com.robert.android.lostpets.presentation.ui.fragments.base

import android.content.Context
import android.support.v4.app.Fragment
import android.view.View
import com.robert.android.lostpets.presentation.presenters.impl.LanguagePresenterImpl
import com.robert.android.lostpets.storage.SettingsRepositoryImpl
import com.robert.android.lostpets.utils.ContextWrapper
import java.util.*

/**
 * Clase base abstracta para todos los fragments que sirve como punto común para los accesos al
 * contexto de la aplicación y el manejo de las vistas antes y después de las operaciones
 * realizadas.
 *
 * @author Robert Ene
 */
abstract class AbstractFragment : Fragment() {

    private lateinit var mContext: Context
    private lateinit var mViewsToHide: MutableList<View>
    private lateinit var mProgressBarLayout: View

    override fun onAttach(context: Context) {
        mViewsToHide = mutableListOf()
        wrapContextLanguage(context)
        super.onAttach(getContext())
    }

    /**
     * Método que devuelve el contexto de la aplicación.
     *
     * @return el contexto de la aplicación.
     */
    override fun getContext(): Context {
        return mContext
    }

    /**
     * Método que establece el layout del spinner (cargando).
     *
     * @param progressBarlayout el layout del spinner.
     */
    fun setProgressBarLayout(progressBarlayout: View) {
        mProgressBarLayout = progressBarlayout
    }

    /**
     * Método que añade las vistas que se deben ocultar durante las operaciones realizadas.
     *
     * @param viewsToHide the views to hide
     */
    fun addViewsToHide(vararg viewsToHide: View) {
        mViewsToHide.addAll(viewsToHide)
    }

    /**
     * Método que oculta las vistas cuando la aplicación está realizando una operación.
     */
    fun showProgress() {
        for (v in mViewsToHide)
            v.visibility = View.GONE

        mProgressBarLayout.visibility = View.VISIBLE
    }

    /**
     * Método que muestra las vistas cuando la aplicación ha realizado una operación.
     */
    fun hideProgress() {
        mProgressBarLayout.visibility = View.GONE

        for (v in mViewsToHide)
            v.visibility = View.VISIBLE
    }

    private fun wrapContextLanguage(context: Context) {
        val languagePresenter = LanguagePresenterImpl(SettingsRepositoryImpl(context))
        val language = languagePresenter.getLanguage()
        mContext = ContextWrapper.wrap(context, Locale(language))
    }
}
