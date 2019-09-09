package com.novardis.test.enums

import com.novardis.test.model.Couple
import org.apache.http.client.methods.CloseableHttpResponse
import java.util.*

interface IMethod {
    fun send(
        url: String,
        body: String? = null,
        parameters: List<Couple> = Collections.emptyList(),
        headers: List<Couple>? = null,
        cookies: List<Couple>? = null
    ): CloseableHttpResponse?
}
