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

import javafx.scene.Node
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.AbstractTool
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.gui.DropFiles
import uk.co.nickthecoder.paratask.gui.DropHelper
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.project.Results
import uk.co.nickthecoder.paratask.project.ToolPane
import java.io.File

class EditorTool() : AbstractTool() {

    override val taskD = TaskDescription("editor", description = "A simple text editor")

    val fileP = MultipleParameter("file") { FileParameter("") }

    val initialTextP = StringParameter("initialText", required = false, hidden = true)

    val findTextP = StringParameter("findText", required = false, hidden = true)

    val matchCaseP = BooleanParameter("matchCase", value = false, hidden = true)

    val useRegexP = BooleanParameter("useRegex", value = false, hidden = true)

    val goToLineP = IntParameter("goToLine", required = false, hidden = true)

    override var tabDropHelper: DropHelper? = DropFiles(arrayOf(TransferMode.COPY)) { _, files ->
        files?.filter { it.isFile() }?.forEach { addFile(it) }
        toolPane?.halfTab?.projectTab?.isSelected = true
        true
    }

    constructor(file: File) : this() {
        fileP.addValue(file)
    }

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
        initialTextP.value = text
    }

    init {
        taskD.addParameters(fileP, initialTextP, goToLineP, findTextP, matchCaseP, useRegexP)
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

    fun addFile(file: File) {
        val innerP = fileP.addValue(file) as FileParameter
        val results = EditorResults(this, file)
        toolPane?.addResults(results)?.isSelected = true
        toolPane?.halfTab?.pushHistory()
    }
}

fun main(args: Array<String>) {
    TaskParser(EditorTool()).go(args)
}
