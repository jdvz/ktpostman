package com.novardis.test.controller

import com.novardis.test.enums.Method
import com.novardis.test.inject.Injector
import com.novardis.test.service.SendService
import com.novardis.test.utils.JsonMapResponseTransformer
import com.novardis.test.model.Couple
import com.novardis.test.utils.Converter
import com.novardis.test.utils.StringResponseTransformer
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TextFormatter
import org.apache.logging.log4j.LogManager
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue
import java.io.File
import java.io.Serializable
import java.util.*
import java.util.function.UnaryOperator

class MainController: Controller() {
    companion object {
        val LOG = LogManager.getLogger(MainController::class.java)
        val URL_REGEX = Regex("^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
        val URL_SYMBOLS_REGEX = Regex("[-a-zA-Z0-9+&@#/%?:.=~_|!,;]*")

        val URL_INITIAL_VALUE = "http://"
        val SET_COOKIE_HEADER = "set-cookie"
    }

    val configuration : ConfigurationController by inject()

    val sendService = Injector.inject(SendService::class.java)

    val methods = FXCollections.observableArrayList<String>(sendService.methods().map { m -> m.name })

    val currentMethod : CurrentMethod = CurrentMethod(Method.GET)
        get() {
            LOG.info("get current method ${field.methodName}")
            return field
        }

    val requestModel : RequestModel = RequestModel(getInitialUrlValue())

    val httpValidator : UnaryOperator<TextFormatter.Change?>
            = UnaryOperator { t -> if (t?.controlNewText?.matches(URL_SYMBOLS_REGEX) == true) t else null }

    fun validateUrl(text: String) : Boolean {
        return text.matches(URL_REGEX)
    }

    fun send() {
        LOG.info("Send request to ${requestModel.url ?: "void"}")
        if (requestModel.url == null) {
            LOG.error("URL not set")
        } else {
            requestModel.parameters.removeAll(Couple::isEmpty)
            requestModel.headers.removeAll(Couple::isEmpty)
            requestModel.cookies.removeAll(Couple::isEmpty)


            val response = currentMethod.getMethod().send(
                requestModel.url!!,
                requestModel.body,
                requestModel.parameters,
                requestModel.headers,
                requestModel.cookies
            )

            if (response != null) {
                requestModel.cookies.addAll(response.allHeaders.filter { it.name.equals(SET_COOKIE_HEADER, true) }
                    .map { Couple.fromArray(it.value.split("=")) })
                requestModel.headers.addAll(response.allHeaders.filter { !it.name.equals(SET_COOKIE_HEADER, true) }.map { Couple(it.name, it.value) })
                requestModel.responseBody = Converter.transformResponse(Injector.inject(StringResponseTransformer::class.java), response)
            }
        }
    }

    fun remove(name: String?, collection: ObservableList<Couple>) {
        val c = collection.find { c -> c.name.equals(name) }
        if (c != null) {
            LOG.info("C will be deleted by $name")
            collection.remove(c)
        }
    }

    fun readBodyFromFile(file: File?) {
        if (file != null && file.isFile()) {
            configuration.fileDirectory = file.parentFile.absolutePath
            requestModel.body = file.readText()
        }
    }

    fun getInitialUrlValue() : String = configuration.urlValue ?: URL_INITIAL_VALUE
}

class CurrentMethod(name: Method) : Serializable {
    val methodProperty = SimpleStringProperty(this, "methodName", name.name)
    var methodName by methodProperty

    fun getMethod(): Method {
        return Method.valueOf(methodName)
    }
}

class RequestModel(initialValue: String) : Serializable {
    val urlProperty = SimpleStringProperty(initialValue)
    var url by urlProperty

    val bodyProperty = SimpleStringProperty()
    var body by bodyProperty

    val responseBodyProperty = SimpleStringProperty()
    var responseBody by responseBodyProperty

    var parameters = FXCollections.observableArrayList(mutableListOf(Couple()))
    var headers = FXCollections.observableArrayList(mutableListOf(Couple()))
    var cookies = FXCollections.observableArrayList(mutableListOf(Couple()))
}
