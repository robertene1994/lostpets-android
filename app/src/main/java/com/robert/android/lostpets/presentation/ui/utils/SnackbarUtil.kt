package com.robert.android.lostpets.presentation.ui.utils

import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.robert.android.lostpets.R

/**
 * Clase de utilidades para el Snackbar (mensajes en la pantalla).
 *
 * @author Robert Ene
 */
class SnackbarUtil {

    companion object {

        /**
         * Método que muestra en la pantalla un mensaje de corta duración.
         *
         * @param view la vista en la que se muestra el mensaje.
         * @param resourceId el id del mensaje que se muestra.
         */
        fun makeShort(view: View, resourceId: Int) {
            make(view, resourceId, Snackbar.LENGTH_SHORT)
        }

        /**
         * Método que muestra en la pantalla un mensaje de larga duración.
         *
         * @param view la vista en la que se muestra el mensaje.
         * @param resourceId el id del mensaje que se muestra.
         */
        fun makeLong(view: View, resourceId: Int) {
            make(view, resourceId, Snackbar.LENGTH_LONG)
        }

        private fun make(view: View, resourceId: Int, duration: Int) {
            val snackbar = Snackbar.make(view, view.context.getString(resourceId), duration)
            val snackBarView = snackbar.view
            snackBarView.setBackgroundColor(
                    ContextCompat.getColor(view.context, R.color.colorPrimary))
            val textView = snackBarView.findViewById(
                    android.support.design.R.id.snackbar_text) as TextView
            textView.setTextColor(
                    ContextCompat.getColor(view.context, R.color.colorSecondaryText))
            textView.gravity = Gravity.CENTER_HORIZONTAL
            textView.maxLines = 3
            snackbar.show()
        }
    }
}
