package com.novardis.test.view

import com.novardis.test.controller.MainController
import com.novardis.test.model.Couple
import com.novardis.test.styles.PostmanStyle
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TabPane
import javafx.scene.control.TextFormatter
import tornadofx.*
import javafx.beans.value.ObservableValue
import javafx.stage.FileChooser
import org.apache.logging.log4j.LogManager


class MasterView: View() {
    companion object {
        val LOG = LogManager.getLogger(MasterView::class.java)
    }
    override val root = borderpane() {
        top {
            menubar {
                menu("File") {
                    item("About")
                    item("Quit", "Shortcut+Q")
                }
            }
        }
        center<TopView>()
        bottom<BottomView>()

        setPrefSize(800.0, 600.0)
    }
}

class TopView: View() {
    companion object {
        val LOG = LogManager.getLogger(TopView::class.java)
    }

    val controller: MainController by inject()
    var methodName = controller.currentMethod.methodProperty.objectBinding { it }

    val urlDisabledProperty = SimpleBooleanProperty(true)

    override val root = vbox(10) {
        padding = insets(10)

        borderpane {
            top {
                hbox {
                    label("Url: ")
                    textfield(controller.urlProperty) {
                        var previousValue = controller.getInitialUrlValue()
                        useMaxWidth = true
                        minWidth = 400.0

                        focusedProperty().addListener { observable: ObservableValue<out Boolean>, oldValue: Boolean, newValue: Boolean ->
                            if (!newValue) {
                                if (controller.validateUrl(this.text)) {
                                    LOG.info("correct url stored from ${previousValue} to ${this.text}")
                                    previousValue = this.text
                                    removeClass(PostmanStyle.errorClass)
                                    urlDisabledProperty.value = false
                                } else {
                                    LOG.info("incorrect url resolved ${this.text}, previous $previousValue restored")
//                                    this.text = previousValue
                                    addClass(PostmanStyle.errorClass)
                                    urlDisabledProperty.value = true
                                }
                            }
                        }
                        textFormatter = TextFormatter<TextFormatter.Change>(controller.httpValidator)

                    }
                    button() {
                        disableProperty().bind(urlDisabledProperty)
                        textProperty().bind(methodName)
                        action {
                            controller.send()
                        }
                    }
                    useMaxWidth = true
                }
            }
            center {
                button("attach a file") {
                    setOnAction {
                        val fileChooser = FileChooser()
                        val file = fileChooser.showOpenDialog(null)
                        controller.readBodyFromFile(file)
                    }
                }
            }
            bottom {
                tabpane {
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

                    tab("request body") {
                        textarea(controller.bodyProperty) {

                        }
                    }
                    tab("response body") {
                        scrollpane {
                            label(controller.responseBodyProperty) {
                                isWrapText = true
                            }
                        }
                    }
                }
            }
        }
    }
}

class BottomView: View() {
    val controller: MainController by inject()

    private val parameters = controller.parameters.observable()
    private val headers = controller.headers.observable()
    private val cookies = controller.cookies.observable()

    override val root = vbox {
        hbox {
            label("Choose method")
            combobox(controller.currentMethod.methodProperty, controller.methods)
        }

        tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

            tab("parameters") {
                borderpane {
                    top {
                        hbox {
                            label("Configure parameters")
                            button("+").action {
                                controller.parameters.add(Couple())
                            }
                            button("-").action {
                                controller.parameters.clear()
                            }
                        }
                    }
                    bottom {
                        tableview(parameters) {
                            isEditable = true
                            column("name", Couple::nameProperty).makeEditable()
                            column("value", Couple::valueProperty).makeEditable().remainingWidth()
                            readonlyColumn("[]", Couple::name).cellFormat {
                                graphic = hbox(spacing = 5) {
                                    button("-").action { controller.remove(it, parameters) }
                                }
                            }

                            columnResizePolicy = SmartResize.POLICY
                        }
                    }
                }
            }

            tab("headers") {
                borderpane {
                    top {
                        hbox {
                            label("headers")
                            button("+").action {
                                controller.headers.add(Couple())
                            }
                            button("-").action {
                                controller.headers.clear()
                            }
                        }
                    }
                    bottom {
                        tableview(headers) {
                            isEditable = true
                            column("name", Couple::nameProperty).makeEditable()
                            column("value", Couple::valueProperty).makeEditable().remainingWidth()
                            readonlyColumn("[]", Couple::name).cellFormat {
                                graphic = hbox(spacing = 5) {
                                    button("-").action { controller.remove(it, headers) }
                                }
                            }

                            columnResizePolicy = SmartResize.POLICY
                        }
                    }
                }
            }

            tab("cookies") {
                borderpane {
                    top {
                        hbox {
                            label("cookies")
                            button("+").action {
                                controller.cookies.add(Couple())
                            }
                            button("-").action {
                                controller.cookies.clear()
                            }
                        }
                    }
                    bottom {
                        tableview(cookies) {
                            isEditable = true
                            column("name", Couple::nameProperty).makeEditable()
                            column("value", Couple::valueProperty).makeEditable().remainingWidth()
                            readonlyColumn("", Couple::name).cellFormat {
                                graphic = hbox(spacing = 5) {
                                    button("-").action { controller.remove(it, parameters) }
                                }
                            }

                            columnResizePolicy = SmartResize.POLICY
                        }
                    }
                }
            }
        }
    }
}

