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

package uk.co.nickthecoder.paratask.tools

import javafx.scene.control.TextArea
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.project.AbstractResults
import uk.co.nickthecoder.paratask.AbstractTool
import java.io.PrintWriter
import java.io.StringWriter

class ExceptionTool : AbstractTool {

    override val taskD = TaskDescription("error", description = "An error has occurred")

    val exception: Exception?

    constructor() : super() {
        exception = null
    }

    constructor(e: Exception) : super() {
        e.printStackTrace()
        exception = e
    }

    override fun run() {
    }

    override fun createResults() = singleResults(ExceptionResults())

    inner class ExceptionResults : AbstractResults(this@ExceptionTool) {

        override val node = TextArea()

        init {
            val writer = StringWriter()
            exception?.printStackTrace(PrintWriter(writer))
            node.text = exception?.message + "\n\n" + writer.toString()

        }

        override fun focus() {
            node.requestFocus()
        }
    }
}
