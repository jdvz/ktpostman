package com.novardis.test.controller

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import tornadofx.*
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

class ConfigurationController : Controller() {
    companion object {
        val LOG : Logger = LogManager.getLogger(ConfigurationController::class.java)
    }

    private val configLocation: Path
    private val configuration: Properties

    var appWidth : Double
        get() = configuration.getProperty("main.app.initial.width").toDouble()
        set(value) {
            configuration.setProperty("main.app.initial.width", value.toString())
        }
    var appHeight : Double
        get() = configuration.getProperty("main.app.initial.height").toDouble()
        set(value) {
            configuration.setProperty("main.app.initial.height", value.toString())
        }

    var fileDirectory : String
        get() = configuration.getProperty("main.app.initial.directory")
        set(value) {
            configuration.setProperty("main.app.initial.directory", value)
        }

    var urlValue : String?
        get() = configuration.getProperty("main.app.initial.url", null)
        set(value) {
            configuration.setProperty("main.app.initial.url", value)
        }

    init {
        configLocation = retrieveLocation()
        configuration = retrieveConfig()
    }

    private fun retrieveLocation(): Path {
        return Paths.get(System.getProperty("user.home"), ".ktpostman", "config.properties")
    }

    private fun retrieveConfig() : Properties {
        if (!Files.exists(configLocation)) {
            if (! Files.exists(configLocation.parent)) {
                Files.createDirectories(configLocation.parent)
            }
            ConfigurationController::class.java.getResourceAsStream("/config.properties").use { input ->
                FileOutputStream(configLocation.toFile()).use { output ->
                    input.copyTo(output)
                }
            }
        }
        val conf = Properties()
        Files.newBufferedReader(configLocation).use { reader ->
            conf.load(reader)
        }
        return conf
    }

    fun close() {
        FileOutputStream(configLocation.toFile()).use { output ->
            LOG.info("exit with configuration")
            configuration.store(output, "")
        }
    }
}