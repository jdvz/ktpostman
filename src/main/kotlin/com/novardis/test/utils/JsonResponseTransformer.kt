package com.novardis.test.utils
import com.fasterxml.jackson.databind.ObjectMapper
import com.novardis.test.controller.MainController
import org.apache.logging.log4j.LogManager
import java.io.Serializable

class JsonResponseTransformer<T : Serializable>(val clazz: Class<T>) : ResponseTransformer<T> {
    companion object {
        val LOG = LogManager.getLogger(MainController::class.java)
    }

    override fun transform(response: String): T? {
        val jsonMapper = ObjectMapper()
        try {
            return jsonMapper.readValue(response, clazz)
        } catch (e: Exception) {
            LOG.error("transform error ${e.message}", e)
        }
        return null
    }
}