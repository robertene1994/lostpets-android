package com.robert.android.lostpets.presentation.ui.utils

import java.security.SecureRandom
import java.util.*

/**
 * Clase de utilidades que permite generar códigos alfanuméricos aleatorios.
 *
 * @author Robert Ene
 */
class CodeGeneratorUtil {

    companion object {
        private const val CODE_GENERATOR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val CODE_LENGTH_DEFAULT = 10
        private const val CODE_LENGTH_MIN = 5
        private const val CODE_LENGTH_MAX = 30

        /**
         * Método que genera de forma aleatoria un código alfanumérico basado en la longitud
         * definida por defecto ({@link #CODE_GENERATOR} CODE_GENERATOR).
         *
         * @return el código alfanumérico generado de forma aleatoria.
         */
        fun random(): String {
            return random(CODE_LENGTH_DEFAULT)
        }

        /**
         * Método que genera de forma aleatoria un código alfanumérico basado en una longitud
         * determinada.
         *
         * @param length la longitud del código generado, que tiene que ser un valor comprendido
         * entre los valores {@link CODE_LENGTH_MIN} mínimo y {@link CODE_LENGTH_MAX} máximo
         * definidos.
         * @return el código alfanumérico generado de forma aleatoria.
         */
        @JvmStatic
        fun random(length: Int?): String {
            var codeLength = length
            if (length == null || length < CODE_LENGTH_MIN || length > CODE_LENGTH_MAX)
                codeLength = CODE_LENGTH_DEFAULT
            val r: Random = SecureRandom()
            val sb = StringBuilder()
            for (i in 0 until codeLength!!)
                sb.append(CODE_GENERATOR[r.nextInt(CODE_GENERATOR.length)])
            return sb.toString()
        }
    }
}
