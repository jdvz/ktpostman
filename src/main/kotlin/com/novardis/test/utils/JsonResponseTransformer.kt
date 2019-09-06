package com.novardis.test.utils

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.logging.log4j.LogManager
import java.io.Serializable

open class JsonResponseTransformer<T : Serializable>(val clazz: Class<T>) : ResponseTransformer<T> {
    companion object {
        val LOG = LogManager.getLogger(JsonResponseTransformer::class.java)
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

class JsonMapResponseTransformer : JsonResponseTransformer<JsonMapWrapper> (JsonMapWrapper::class.java) {
    override fun transform(response: String): JsonMapWrapper? {
        val jsonMapper = ObjectMapper()
        try {
            return jsonMapper.readValue(response, JsonMapWrapperReference())
        } catch (e: Exception) {
            LOG.error("transform error ${e.message}", e)
        }
        return null
    }
}

class JsonMapWrapper : HashMap<String, String> ()  {
    fun toMap() : Map<String, String> {
        return this
    }
}

class JsonMapWrapperReference : TypeReference<JsonMapWrapper> ()