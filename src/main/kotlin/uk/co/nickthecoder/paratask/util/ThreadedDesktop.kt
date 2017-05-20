package uk.co.nickthecoder.paratask.util

import java.awt.Desktop
import java.io.File
import java.net.URI

class ThreadedDesktop {

    companion object {
        val instance = ThreadedDesktop()
    }

    val desktop: Desktop = Desktop.getDesktop()

    fun open(file: File) {
        val thread = Thread { desktop.open(file) }
        thread.name = "TheadedDesktop.open"
        thread.isDaemon = true
        thread.start()
    }

    fun edit(file: File) {
        val thread = Thread { desktop.edit(file) }
        thread.name = "TheadedDesktop.open"
        thread.isDaemon = true
        thread.start()
    }

    fun browse(uri: URI) {
        val thread = Thread { desktop.browse(uri) }
        thread.name = "TheadedDesktop.browse"
        thread.isDaemon = true
        thread.start()
    }

    fun browse(file: File) {
        browse(file.toURI())
    }

}