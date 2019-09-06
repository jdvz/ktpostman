package com.novardis.test.styles

import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.FontWeight
import tornadofx.CssRule.Companion.c
import tornadofx.MultiValue
import tornadofx.Stylesheet
import tornadofx.cssclass

class PostmanStyle : Stylesheet() {
    companion object {
        val errorClass by cssclass()
        val defaultClass by cssclass()
    }

    init {
        errorClass {
            fontWeight = FontWeight.BOLD
            baseColor = Color.ORANGE
        }
    }
}