package com.novardis.test.enums

import com.novardis.test.model.Couple
import com.novardis.test.utils.ResponseTransformer
import java.io.Serializable
import java.util.*

interface IMethod {
    fun <T:Serializable> send(
        url: String,
        transformer: ResponseTransformer<T>,
        body: String? = null,
        parameters: List<Couple> = Collections.emptyList(),
        headers: List<Couple>? = null,
        cookies: List<Couple>? = null
    ): T?
}
