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
    }
}