package com.novardis.test.view

import com.novardis.test.controller.ConfigurationController
import com.novardis.test.controller.MainController
import com.novardis.test.model.Couple
import com.novardis.test.styles.PostmanStyle
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.control.TabPane
import javafx.scene.control.TextFormatter
import tornadofx.*
import javafx.beans.value.ObservableValue
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import org.apache.logging.log4j.LogManager
import java.io.File


class MasterView: View() {
    companion object {
        val LOG = LogManager.getLogger(MasterView::class.java)
    }

    init {
        importStylesheet(PostmanStyle::class)
    }

    val configuration : ConfigurationController by inject()

    override val root = borderpane() {
        top {
            menubar {
                menu("File") {
                    item("About")
                    item("Quit", "Shortcut+Q").action {
                        Platform.runLater(closeEvent())
                    }
                }
            }
        }
        center<TopView>()
        bottom<BottomView>()

        setPrefSize(configuration.appWidth, configuration.appHeight)
        setMaxSize(1000.0, 800.0)

        widthProperty().addListener {observable: ObservableValue<out Number>, oldValue: Number, newValue: Number ->
            configuration.appWidth = newValue.toDouble()
        }
        heightProperty().addListener {observable: ObservableValue<out Number>, oldValue: Number, newValue: Number ->
            configuration.appHeight = newValue.toDouble()
        }

        primaryStage.setOnCloseRequest {
            Platform.runLater(closeEvent())
        }
    }

    private fun closeEvent(): () -> Unit {
        return {
            LOG.info("exit")
            configuration.close()
            Platform.exit()
            System.exit(0)
        }
    }
}

class TopView: View() {
    companion object {
        val LOG = LogManager.getLogger(TopView::class.java)
    }

    val controller: MainController by inject()
    val configuration : ConfigurationController by inject()

    var methodName = controller.currentMethod.methodProperty.objectBinding { it }

    val urlDisabledProperty = SimpleBooleanProperty(controller.validateUrl(controller.getInitialUrlValue()))
    var responsePane: Tab by singleAssign<Tab>()
    var scrollPane : ScrollPane by singleAssign<ScrollPane>()

    init {
        controller.responseBodyProperty.onChange { responsePane.select() }
    }

    override val root = vbox{
//        addClass("layoutClass")

        borderpane {
            top {
                id = "top-pane-wrapper"
                hbox {
                    id = "top-pane"
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
                                    configuration.urlValue = this.text
                                    removeClass(PostmanStyle.errorClass)
                                    urlDisabledProperty.value = false
                                } else {
                                    LOG.info("incorrect url resolved ${this.text}, previous $previousValue might be restored")
                                    addClass(PostmanStyle.errorClass)
                                    urlDisabledProperty.value = true

                                }
                            }
                        }
                        textFormatter = TextFormatter<TextFormatter.Change>(controller.httpValidator)

                    }
                    hbox {
                        label("Choose method")
                        combobox(controller.currentMethod.methodProperty, controller.methods)
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
            bottom {
                id = "bottom-pane-wrapper"
                tabpane {
                    id = "bottom-pane"
                    val heightProperty = heightProperty()
                    val initialHeight = height
                    tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

                    tab("request body") {
                        vbox {
                            button("attach a file") {
                                setOnAction {
                                    val fileChooser = FileChooser()
                                    if (configuration.fileDirectory.isNotEmpty()) {
                                        fileChooser.initialDirectory = File(configuration.fileDirectory)
                                    }
                                    val file = fileChooser.showOpenDialog(null)
                                    controller.readBodyFromFile(file)
                                }
                            }
                            textarea(controller.bodyProperty) {
                                maxHeightProperty().bind(heightProperty)
                            }
                        }
                    }
                    responsePane = tab("response body") {
                        vbox {
                            addClass("markerClass")
                            scrollPane = scrollpane {
//                                vbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
                                label(controller.responseBodyProperty) {
                                    heightProperty().addListener {observable: ObservableValue<out Number>, oldValue: Number, newValue: Number ->
                                        scrollPane.vvalue = newValue.toDouble()
                                    }
                                }
                                maxHeightProperty().bind(heightProperty)
                                maxWidth = Double.MAX_VALUE
                            }
                            VBox.setVgrow(scrollPane, Priority.ALWAYS)
                        }
                    }
/*
                    heightProperty().addListener { observable : ObservableValue<out Number>, oldValue, newValue ->
                        scrollPane.maxHeight = newValue.toDouble()
                    }
*/
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
                                    button("-").action { controller.remove(it, cookies) }
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

