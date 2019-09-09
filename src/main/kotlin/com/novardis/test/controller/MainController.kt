package com.novardis.test.controller

import com.novardis.test.enums.Method
import com.novardis.test.inject.Injector
import com.novardis.test.service.SendService
import com.novardis.test.utils.JsonMapResponseTransformer
import com.novardis.test.model.Couple
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
import java.util.*
import java.util.function.UnaryOperator

class MainController: Controller() {
    companion object {
        val LOG = LogManager.getLogger(MainController::class.java)
        val URL_REGEX = Regex("^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")
        val URL_SYMBOLS_REGEX = Regex("[-a-zA-Z0-9+&@#/%?:.=~_|!,;]*")

        val URL_INITIAL_VALUE = "http://"
    }

    val configuration : ConfigurationController by inject()

    val sendService = Injector.inject(SendService::class.java)

    val methods = FXCollections.observableArrayList<String>(sendService.methods().map { m -> m.name })
    var parameters = mutableListOf(Couple())
    var headers = mutableListOf(Couple())
    var cookies = mutableListOf(Couple())

    val currentMethod : CurrentMethod = CurrentMethod(Method.GET)
        get() {
            LOG.info("get current method ${field.methodName}")
            return field
        }

    val urlProperty = SimpleStringProperty(getInitialUrlValue())
    var url by urlProperty

    val bodyProperty = SimpleStringProperty()
    var body by bodyProperty

    val responseBodyProperty = SimpleStringProperty()
    var responseBody by responseBodyProperty

    val httpValidator : UnaryOperator<TextFormatter.Change?>
            = UnaryOperator { t -> if (t?.controlNewText?.matches(URL_SYMBOLS_REGEX) == true) t else null }

    fun validateUrl(text: String) : Boolean {
        return text.matches(URL_REGEX)
    }

    fun send() {
        LOG.info("Send request to ${url ?: "void"}")
        if (url == null) {
            LOG.error("URL not set")
        } else {
            parameters.removeAll(Couple::isEmpty)
            headers.removeAll(Couple::isEmpty)
            cookies.removeAll(Couple::isEmpty)
            responseBody = currentMethod.getMethod().send(
                url!!,
                Injector.inject(StringResponseTransformer::class.java),
                body,
                parameters,
                headers,
                cookies
            )
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
            body = file.readText()
        }
    }

    fun getInitialUrlValue() : String = configuration.urlValue ?: URL_INITIAL_VALUE
}

class CurrentMethod(name: Method) {
    val methodProperty = SimpleStringProperty(this, "methodName", name.name)
    var methodName by methodProperty

    fun getMethod(): Method {
        return Method.valueOf(methodName)
    }
}
