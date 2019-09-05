package com.novardis.test.utils

import java.io.Serializable

interface ResponseTransformer<T:Serializable> {
    fun transform(response: String): T?
}
