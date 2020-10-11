package com.robert.android.lostpets.network.converter

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Clase que extiende la clase Converter.Factory. Su función consiste en manejar la deserialización
 * de valores null o vacíos.
 *
 * @author Robert Ene
 */
class NullOrEmptyConverterFactory : Converter.Factory() {

    fun converterFactory() = this
    override fun responseBodyConverter(type: Type, annotations: Array<out Annotation>,
                                       retrofit: Retrofit) = object : Converter<ResponseBody, Any?> {
        val nextResponseBodyConverter
                = retrofit.nextResponseBodyConverter<Any?>(converterFactory(), type, annotations)
        override fun convert(value: ResponseBody) =
                if (value.contentLength() > 0) nextResponseBodyConverter.convert(value)
                else null
    }
}
