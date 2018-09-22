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

import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.AbstractTool
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.gui.DropFiles
import uk.co.nickthecoder.paratask.gui.DropHelper
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.project.Results
import java.io.File

private val MAX_FILE_SIZE = 1_000_000

class EditorTool() : AbstractTool() {

    override val taskD = TaskDescription("editor", description = "A simple text editor")

    val filesP = MultipleParameter("file") { FileParameter("aFile") }

    val ignoreFileSizeCheckP = BooleanParameter("ignoreFileSizeCheck", value = false)

    val initialTextP = StringParameter("initialText", required = false)

    override var tabDropHelper: DropHelper? = DropFiles(arrayOf(TransferMode.COPY)) { _, files ->
        files.filter { it.isFile }.forEach { addFile(it) }
        toolPane?.halfTab?.projectTab?.isSelected = true
    }

    constructor(file: File) : this() {
        filesP.addValue(file)
    }

    constructor(vararg files: File) : this() {
        for (file in files) {
            filesP.addValue(file)
        }
    }

    constructor(files: List<File>) : this() {
        for (file in files) {
            filesP.addValue(file)
        }
    }

    constructor(text: String) : this() {
        initialTextP.value = text
    }

    init {
        ignoreFileSizeCheckP.hidden = true
        initialTextP.hidden = true

        taskD.addParameters(filesP, ignoreFileSizeCheckP, initialTextP)
        taskD.unnamedParameter = filesP
    }

    override fun customCheck() {
        super.customCheck()
        if (ignoreFileSizeCheckP.value != true) {
            filesP.innerParameters.forEach { fileP ->
                if (fileP.value!!.length() > MAX_FILE_SIZE) {
                    ignoreFileSizeCheckP.hidden = false
                    throw ParameterException(fileP, "File is too large to open in the text editor.")
                }
            }
        }
    }

    override fun run() {
    }

    override fun createResults(): List<Results> {
        if (filesP.value.isEmpty()) {
            return singleResults(EditorResults(this, initialTextP.value))
        } else {
            return filesP.value.map { EditorResults(this, it) }
        }
    }

    fun addFile(file: File) {
        val results = EditorResults(this, file)
        toolPane?.addResults(results)?.isSelected = true
        toolPane?.halfTab?.pushHistory()
    }
}

fun main(args: Array<String>) {
    TaskParser(EditorTool()).go(args)
}
