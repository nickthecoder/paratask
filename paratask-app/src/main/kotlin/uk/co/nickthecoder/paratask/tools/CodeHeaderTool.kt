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

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.misc.WrappedFile
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.table.BooleanColumn
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.ListTableTool
import uk.co.nickthecoder.paratask.util.FileLister
import java.io.File

class CodeHeaderTool : ListTableTool<CodeHeaderTool.ProcessedFile>() {

    override val taskD = TaskDescription("codeHeader", description = "Adds a Copyright notice to the top of source code file")

    val directoriesP = MultipleParameter("directories") {
        FileParameter("directory", expectFile = false, mustExist = true)
    }

    val extensionsP = MultipleParameter("extensions", value = listOf("java", "kt")) { StringParameter("") }

    val showProcessedFilesP = BooleanParameter("showProcessedFiles", value = false)

    val headerTestP = StringParameter("headerTest",
            value = "This program is free software",
            description = "Check if the file already has a header by looking for this text")

    val withinLinesP = IntParameter("withinLines", description = "Within this many lines", value = 10)

    val depthP = IntParameter("depth", description = "Depth of recusrive search", value = 10)

    val headerTextP = StringParameter("headerText", rows = 10, columns = 50, value =
    """<PROGRAM NAME AND DESCRIPTION>
Copyright (C) <YEAR> <AUTHOR>

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
""")

    private var containsText by headerTestP

    init {
        taskD.addParameters(directoriesP, extensionsP, showProcessedFilesP, depthP, headerTestP, withinLinesP, headerTextP)
    }

    override fun loadProblem(parameterName: String, expression: String?, stringValue: String?) {
        if (parameterName == "directory") {
            if (expression == null) {
                directoriesP.value = listOf(File(stringValue))
            } else {
                directoriesP.clear()
                directoriesP.newValue().expression = expression
            }
            return
        }
        super.loadProblem(parameterName, expression, stringValue)
    }

    override fun createColumns(): List<Column<ProcessedFile, *>> {
        val columns = mutableListOf<Column<ProcessedFile, *>>()

        columns.add(BooleanColumn<ProcessedFile>("processed") { it.processed })
        columns.add(Column<ProcessedFile, String>("name") { it.file.name })
        columns.add(Column<ProcessedFile, File>("path") { it.file })

        return columns
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        // Use the project's standard header if one has been set.
        project?.retrievePreferences(this)
    }

    override fun run() {
        project?.storePreferences(this)

        list.clear()
        containsText = headerTestP.value
        val lister = FileLister(depth = depthP.value!!, extensions = extensionsP.value)
        directoriesP.value.forEach {
            val processedFiles = lister.listFiles(it!!).map { ProcessedFile(it) }
            if (showProcessedFilesP.value == true) {
                list.addAll(processedFiles)
            } else {
                list.addAll(processedFiles.filter { it.processed == false })
            }
        }

    }

    fun isProcessed(file: File): Boolean {
        println("Checking $file")

        val reader = file.bufferedReader()
        reader.use {
            for (i in 1..withinLinesP.value!!) {
                val line = reader.readLine()
                line ?: return false
                if (line.contains(containsText)) {
                    return true
                }
            }
        }
        return false
    }


    inner class ProcessedFile(file: File) : WrappedFile(file) {
        var processed = false

        init {
            processed = isProcessed(file)
        }

        fun addHeader() {
            if (!processed) {
                val headerText = "/*\n${headerTextP.value}\n*/\n"
                val contents = file.readText()
                file.writeText(headerText + contents)
                processed = true
            }
        }
    }
}


fun main(args: Array<String>) {
    TaskParser(CodeHeaderTool()).go(args)
}
