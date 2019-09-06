package com.novardis.test.utils

import com.novardis.test.inject.Bean
import java.io.Serializable

interface ResponseTransformer<T:Serializable> : Bean {
    fun transform(response: String): T?
}
