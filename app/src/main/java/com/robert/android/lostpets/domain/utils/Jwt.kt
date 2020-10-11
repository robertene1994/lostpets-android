package com.robert.android.lostpets.domain.utils

import com.robert.android.lostpets.network.converter.GsonSerializer
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.Date
import kotlin.math.floor

/**
 * Clase de utilidades para la autenticación del usuario, basado en la descodificación del token
 * JWT.
 *
 * @author Robert Ene
 */
class Jwt() {

    internal class JWTPayload(val exp: Long)

    private lateinit var mJwt: String

    constructor(token: String) : this() {
        removeTokenPrefix(token)
    }

    /**
     * Método que determina si un token JWT ha caducado.
     *
     * @return true si el token ha caducado, false de lo contrario.
     */
    fun isExpired(): Boolean {
        return try {
            val payload = decode(mJwt) ?: return true
            return isExpired(Date(payload.exp * 1000))
        } catch (e: Exception) {
            true
        }
    }

    private fun removeTokenPrefix(token: String) {
        mJwt = token.substring(token.indexOf(" ") + 1, token.length - 1)
    }

    private fun decode(token: String): JWTPayload? {
        val parts: Array<String> = token.split(".").toTypedArray()
        val payload = base64Decode(parts[1]) ?: return null
        return GsonSerializer.instance.fromJson(payload, JWTPayload::class.java)
    }

    private fun base64Decode(string: String): String? {
        return try {
            String(Base64.getDecoder().decode(string), StandardCharsets.UTF_8)
        } catch (e: Exception) {
            null
        } as String
    }

    private fun isExpired(exp: Date?): Boolean {
        val today = Date((floor(Date().time / 1000.toDouble()) * 1000).toLong())
        return exp != null && today.after(exp)
    }
}
