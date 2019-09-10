package com.novardis.test.styles

import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.CssRule.Companion.c
import tornadofx.*

class PostmanStyle : Stylesheet() {
    companion object {
        val errorClass by cssclass()
        val defaultClass by cssclass()
        val markerClass by cssclass()
        val layoutClass by cssclass()
    }

    init {
        errorClass {
            fontWeight = FontWeight.BOLD
            baseColor = Color.ORANGE
        }

        defaultClass {
            fontWeight = FontWeight.NORMAL
        }

        markerClass {
            backgroundColor += Color.LIGHTCYAN
        }

        layoutClass {
        }

        s("#top-pane") {
            maxHeight = 100.px
        }

        s("#top-pane-wrapper") {
            maxHeight = 100.px
            minHeight = 80.px
        }

        s("#bottom-pane-wrapper") {
            maxHeight = 400.px
            minHeight = 180.px
        }

        s("#bottom-pane") {
            maxHeight = 400.px
            minHeight = 180.px

            s(".tab") {
            }

            s(".vbox") {
            }
        }
    }
}