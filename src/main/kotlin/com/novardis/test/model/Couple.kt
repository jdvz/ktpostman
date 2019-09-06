package com.novardis.test.model

import org.apache.http.message.BasicNameValuePair
import tornadofx.getProperty
import tornadofx.property
import java.io.Serializable

class Couple(name: String = "", value: String = "") : Serializable {
    var name by property(name)
    fun nameProperty() = getProperty(Couple::name)

    var value by property(value)
    fun valueProperty() = getProperty(Couple::value)

    fun toBasicNameValuePair() = BasicNameValuePair(name, value)
    fun isEmpty() = name.isNullOrBlank() && value.isNotBlank()
}