package com.robert.android.lostpets.utils

import android.content.Context
import android.os.LocaleList
import java.util.*

/**
 * Clase que envuelve el contexto de la aplicación actualizando el idioma en función de las
 * preferencias del usuario.
 *
 * @author Robert Ene
 */
class ContextWrapper(base: Context) : android.content.ContextWrapper(base) {

    companion object {
        /**
         * Método que envuelve el contexto de la aplicación actualizando el idioma en función de las
         * preferencias del usuario (o por defecto si el usuario no ha establecido preferencias).
         *
         * @param ctx el contexto de la aplicación.
         * @param locale  el objeto locale que refleja el idioma que se debe aplicar al contexto.
         * @return el nuevo contexto envuelto y actualizado según el idioma establecido.
         */
        fun wrap(ctx: Context, locale: Locale): ContextWrapper {
            var context = ctx
            val configuration = context.resources.configuration
            configuration.setLocale(locale)
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            configuration.locales = localeList
            context = context.createConfigurationContext(configuration)
            return ContextWrapper(context)
        }
    }
}
