package com.novardis.test.enums

import com.novardis.test.utils.ResponseTransformer
import java.io.Serializable

enum class Method: IMethod {
    GET {
        override fun <T : Serializable> send(
            url: String,
            transformer: ResponseTransformer<T>,
            params: Map<String, String>
        ): T {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    },
    POST {
        override fun <T : Serializable> send(
            url: String,
            transformer: ResponseTransformer<T>,
            params: Map<String, String>
        ): T {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    },
    PUT {
        override fun <T : Serializable> send(
            url: String,
            transformer: ResponseTransformer<T>,
            params: Map<String, String>
        ): T {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    },
    DELETE {
        override fun <T : Serializable> send(
            url: String,
            transformer: ResponseTransformer<T>,
            params: Map<String, String>
        ): T {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    };
}