package com.novardis.test.service

import com.novardis.test.enums.Method
import java.util.*

class SendService: IService {
    fun methods(): Collection<Method> {
        return Method.values().asList()
    }
}
