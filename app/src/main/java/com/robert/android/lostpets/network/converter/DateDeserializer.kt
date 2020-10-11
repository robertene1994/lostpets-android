package com.robert.android.lostpets.network.converter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

/**
 * Clase que extiende la clase JsonDeserializer. Su función consiste en manejar la deserialización
 * de fechas.
 *
 * @author Robert Ene
 */
class DateDeserializer : JsonDeserializer<Date> {

    private val mDateFormat: SimpleDateFormat = SimpleDateFormat(
            "MMM dd, yyyy HH:mm:ss", Locale.ENGLISH)

    override fun deserialize(json: JsonElement, typeOfT: Type,
                             context: JsonDeserializationContext): Date {

        return if (json.asString.toLongOrNull() != null)
            Date(json.asLong)
        else
            try {
                return mDateFormat.parse(json.asString)
            } catch (e: Exception) {
                return Date()
            }
    }
}
