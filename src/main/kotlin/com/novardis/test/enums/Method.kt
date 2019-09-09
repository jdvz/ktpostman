package com.novardis.test.enums

import com.novardis.test.inject.CookieWrapper
import com.novardis.test.inject.Injector
import com.novardis.test.model.Couple
import com.novardis.test.utils.Converter
import com.novardis.test.utils.ResponseTransformer
import org.apache.http.HttpResponse
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.*
import org.apache.http.client.utils.URIBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.cookie.BasicClientCookie
import java.io.Serializable

enum class Method: IMethod {
    GET {
        override fun send(
            url: String,
            body: String?,
            parameters: List<Couple>,
            headers: List<Couple>?,
            cookies: List<Couple>?
        ): CloseableHttpResponse? {
            return createRequest(headers, cookies) {
                val urlBuilder = URIBuilder(url)
                parameters.forEach { couple -> urlBuilder.addParameter(couple.name, couple.value) }
                HttpGet(urlBuilder.build())
            }
        }
    },
    POST {
        override fun send(
            url: String,
            body: String?,
            parameters: List<Couple>,
            headers: List<Couple>?,
            cookies: List<Couple>?
        ): CloseableHttpResponse? {
            return createRequest(headers, cookies) {
                val urlBuilder = URIBuilder(url)
                val post = HttpPost(urlBuilder.build())
                if (!body.isNullOrBlank()) {
                    post.entity = StringEntity(body)
                } else {
                    post.entity = UrlEncodedFormEntity(parameters.map(Couple::toBasicNameValuePair))
                }
                post
            }
        }
    },
    PUT {
        override fun send(
            url: String,
            body: String?,
            parameters: List<Couple>,
            headers: List<Couple>?,
            cookies: List<Couple>?
        ): CloseableHttpResponse? {
            return createRequest(headers, cookies) {
                val urlBuilder = URIBuilder(url)
                val post = HttpPut(urlBuilder.build())
                post.entity = UrlEncodedFormEntity(parameters.filter { couple -> !couple.isEmpty() }
                    .map(Couple::toBasicNameValuePair))
                post
            }
        }
    },
    DELETE {
        override fun send(
            url: String,
            body: String?,
            parameters: List<Couple>,
            headers: List<Couple>?,
            cookies: List<Couple>?
        ): CloseableHttpResponse? {
            return createRequest(headers, cookies) {
                val urlBuilder = URIBuilder(url)
                parameters.forEach { couple -> if (!couple.isEmpty()) { urlBuilder.addParameter(couple.name, couple.value) } }
                HttpDelete(urlBuilder.build())
            }
        }
    },
    HEAD {
        override fun send(
            url: String,
            body: String?,
            parameters: List<Couple>,
            headers: List<Couple>?,
            cookies: List<Couple>?
        ): CloseableHttpResponse? {
            return createRequest(headers, cookies) {
                val urlBuilder = URIBuilder(url)
                parameters.forEach { couple -> if (!couple.isEmpty()) { urlBuilder.addParameter(couple.name, couple.value) } }
                HttpHead(urlBuilder.build())
            }
        }
    };

    protected fun <F : HttpRequestBase> createRequest(headers: List<Couple>?,
                                                      cookies: List<Couple>?,
                                                      function: () -> F) : CloseableHttpResponse? {
        val client = HttpClientBuilder.create().build()
        val request = function()
        val store = Injector.inject(CookieWrapper::class.java)

        request.addHeader("User-Agent", "Test/0.1")
        headers?.forEach { e ->
            if (!e.isEmpty()) request.addHeader(e.name, e.value)
        }
        cookies?.forEach { e ->
            if (!e.isEmpty()) {
                val cookie = BasicClientCookie(e.name, e.value)
                cookie.setDomain(request.uri.getHost())
                cookie.setPath("/")
                store.addCookie(cookie)
            }
        }

        return client.execute(request)
    }
}