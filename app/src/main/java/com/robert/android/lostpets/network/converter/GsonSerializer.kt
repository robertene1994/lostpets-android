package com.robert.android.lostpets.network.converter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.*

/**
 * Clase que genera una instancia (singleton) para el serializador/deserializador JSON basado en
 * Gson.
 *
 * @author Robert Ene
 */
class GsonSerializer {

    companion object {
        val instance: Gson by lazy { GsonSerializer().mGson }
    }

    private var mGson: Gson

    init {
        val gsonBuilder = GsonBuilder().setLenient()
        gsonBuilder.registerTypeAdapter(Date::class.java, DateDeserializer())
        mGson = gsonBuilder.create()
    }
}
