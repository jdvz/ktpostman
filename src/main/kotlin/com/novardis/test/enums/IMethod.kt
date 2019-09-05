package com.novardis.test.enums

import com.novardis.test.utils.ResponseTransformer
import java.io.Serializable

interface IMethod {
    fun <T:Serializable> send(url:String, transformer: ResponseTransformer<T>, params: Map<String, String>): T
}
