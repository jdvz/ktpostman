package com.novardis.test.utils

class StringResponseTransformer : ResponseTransformer<String> {
    override fun transform(response: String): String? {
        return response
    }
}