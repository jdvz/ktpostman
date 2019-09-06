package com.novardis.test.utils

import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.util.EntityUtils
import org.apache.logging.log4j.LogManager
import java.io.Serializable

class Converter {
    companion object {
        val LOG = LogManager.getLogger(Converter::class.java)

        fun <T : Serializable> transformResponse(transformer: ResponseTransformer<T>, httpResponse: HttpResponse) : T? {
            val responseCode = httpResponse.statusLine.statusCode
            if (HttpStatus.SC_OK <= responseCode && HttpStatus.SC_ACCEPTED >= responseCode) {
                val content = EntityUtils.toString(httpResponse.entity)
                LOG.debug(content)
                return transformer.transform(content)
            } else {
                LOG.error("Incorrect response code $responseCode")
            }
            return null
        }
    }
}
