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

package uk.co.nickthecoder.paratask.tools.editor

import uk.co.nickthecoder.paratask.AbstractTool
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.project.Results
import java.io.File

class EditorTool() : AbstractTool() {

    override val taskD = TaskDescription("editor", description = "A simple text editor")

    val fileP = MultipleParameter("file") { FileParameter("") }

    val initialTextP = StringParameter("initialText", required = false)

    constructor(vararg files: File) : this() {
        for (file in files) {
            fileP.addValue(file)
        }
    }

    constructor(files: List<File>) : this() {
        for (file in files) {
            fileP.addValue(file)
        }
    }

    constructor(text: String) : this() {
        println("Constructed with ${text}")
        initialTextP.value = text
    }

    init {
        taskD.addParameters(fileP, initialTextP)
        initialTextP.hidden = true
    }


    override fun run() {
    }

    override fun createResults(): List<Results> {
        if (fileP.value.isEmpty()) {
            return singleResults(EditorResults(this, initialTextP.value))
        } else {
            return fileP.value.map { EditorResults(this, it) }
        }
    }

}

fun main(args: Array<String>) {
    TaskParser(EditorTool()).go(args)
}
