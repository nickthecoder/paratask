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
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.project.Results
import java.io.File

val MAX_FILE_SIZE = 20_000

class EditorTool() : AbstractTool() {

    override val taskD = TaskDescription("editor", description = "A simple text editor")

    val filesP = MultipleParameter("file") { FileParameter("") }

    val initialTextP = StringParameter("initialText", required = false)

    val findTextP = StringParameter("findText", required = false)

    val matchCaseP = BooleanParameter("matchCase", value = false)

    val useRegexP = BooleanParameter("useRegex", value = false)

    val goToLineP = IntParameter("goToLine", required = false)

    override var tabDropHelper: DropHelper? = DropFiles(arrayOf(TransferMode.COPY)) { _, files ->
        files.filter { it.isFile() }.forEach { addFile(it) }
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
        initialTextP.hidden = true
        goToLineP.hidden = true
        findTextP.hidden = true
        matchCaseP.hidden = true
        useRegexP.hidden = true
        taskD.addParameters(filesP, initialTextP, goToLineP, findTextP, matchCaseP, useRegexP)
    }

    override fun customCheck() {
        super.customCheck()
        filesP.innerParameters.forEach { fileP ->
            if (fileP.value!!.length() > MAX_FILE_SIZE) {
                throw ParameterException(fileP, "File is too large to open in the text editor.")
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
