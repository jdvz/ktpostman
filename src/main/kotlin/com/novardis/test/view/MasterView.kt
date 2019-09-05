package com.novardis.test.view

import com.novardis.test.controller.MainController
import com.novardis.test.enums.Method
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TextField
import tornadofx.*

class MasterView: View() {
    override val root = borderpane() {
        top<TopView>()
        bottom<BottomView>()
    }
}

class TopView: View() {
    val controller: MainController by inject()
    var methodName = controller.currentMethod.methodProperty.objectBinding { it }

    override val root = vbox {
        label("Top View: ")
        label(methodName)
    }
}

class BottomView: View() {
    val controller: MainController by inject()

    override val root = vbox {
        label("Choose method")
        combobox(controller.currentMethod.methodProperty, controller.methods)
    }
}

