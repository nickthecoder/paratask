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

import javafx.application.Platform
import javafx.stage.Stage

/**
 * My application may close the last remaining JavaFX stage, and then try to create another one.
 * This class ensures that JavaFX does not terminate when it shouldn't
 */
object AutoExit {

    val log = false

    private var enabled = true

    init {
        Platform.setImplicitExit(false)
    }

    var resourceCounter = 0

    fun disable() {
        Platform.setImplicitExit(true)
        enabled = false
    }

    fun inc(message: String) {
        privateInc()
        if (log) printLog("inc $message $resourceCounter")
    }

    private fun privateInc() {
        resourceCounter++
    }

    fun dec(message: String) {
        privateDec()
        if (log) printLog("dec $message $resourceCounter")
    }

    private fun privateDec() {
        resourceCounter--
        if (resourceCounter == 0 && enabled) {
            Platform.exit()
            // I need this when using Swing from within JavaFX. JEditTerm uses Swing.
            System.exit(0)
        }
    }

    fun show(stage: Stage) {
        privateInc()
        stage.show()
        if (log) printLog("Show $resourceCounter $stage")
        stage.setOnHiding {
            privateDec()
            if (log) printLog("Hide $resourceCounter $stage")
        }
    }

    fun printLog(message: String) {
        println(message)
    }

}
