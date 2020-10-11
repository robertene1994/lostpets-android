package com.robert.android.lostpets.presentation.ui.utils

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Clase de utilidades para el Keyboard (teclado virtual).
 *
 * @author Robert Ene
 */
class KeyboardUtil {

    companion object {

        /**
         * Método que oculta el teclado virtual.
         *
         * @param context el contexto de la aplicación.
         * @param view la vista en la que se oculta el teclado virtual.
         */
        fun hideKeyboard(context: Context, view: View) {
            val imm
                    = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)
        }
    }
}
