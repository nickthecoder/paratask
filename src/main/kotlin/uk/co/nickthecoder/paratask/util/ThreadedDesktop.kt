/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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