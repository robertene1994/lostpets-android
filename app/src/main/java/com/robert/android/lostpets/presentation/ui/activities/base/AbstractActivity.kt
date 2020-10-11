package com.robert.android.lostpets.presentation.ui.activities.base

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.robert.android.lostpets.presentation.presenters.impl.LanguagePresenterImpl
import com.robert.android.lostpets.storage.SettingsRepositoryImpl
import com.robert.android.lostpets.utils.ContextWrapper
import java.util.*

/**
 * Clase base abstracta para todas las activities que sirve como punto común para los accesos al
 * contexto de la aplicación y el manejo de las vistas antes y después de las operaciones
 * realizadas.
 *
 * @author Robert Ene
 */
abstract class AbstractActivity : AppCompatActivity() {

    private lateinit var mContext: Context
    private lateinit var mViewsToHide: MutableList<View>
    private lateinit var mProgressBarLayout: View

    override fun attachBaseContext(context: Context) {
        mViewsToHide = mutableListOf()
        wrapContextLanguage(context)
        super.attachBaseContext(getContext())
    }

    /**
     * Método que devuelve el contexto de la aplicación.
     *
     * @return el contexto de la aplicación.
     */
    protected fun getContext(): Context {
        return mContext
    }

    /**
     * Método que establece el layout del spinner (cargando).
     *
     * @param progressBarLayout el layout del spinner.
     */
    protected fun setProgressBarLayout(progressBarLayout: View) {
        mProgressBarLayout = progressBarLayout
    }

    /**
     * Método que añade las vistas que se deben ocultar durante las operaciones realizadas.
     *
     * @param viewsToHide the views to hide
     */
    protected fun addViewsToHide(vararg viewsToHide: View) {
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
