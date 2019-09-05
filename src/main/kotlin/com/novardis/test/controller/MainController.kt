package com.novardis.test.controller

import com.novardis.test.enums.Method
import com.novardis.test.inject.Injector
import com.novardis.test.service.SendService
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import org.apache.logging.log4j.LogManager
import tornadofx.Controller
import tornadofx.getValue
import tornadofx.setValue

class MainController: Controller() {
    companion object {
        val LOG = LogManager.getLogger(MainController::class.java)
    }

    val sendService = Injector.inject(SendService::class.java)

    val methods = FXCollections.observableArrayList<String>(sendService.methods().map { m -> m.name })

    val currentMethod : CurrentMethod = CurrentMethod(this)
        get() {
            LOG.info("get current method ${field.methodName}")
            return field
        }
}

class CurrentMethod(controller: MainController, name: String? = null) {
    val methodProperty = SimpleStringProperty(this, "methodName", name)
    var methodName by methodProperty

    fun getMethod(): Method {
        return Method.valueOf(methodName)
    }
}
