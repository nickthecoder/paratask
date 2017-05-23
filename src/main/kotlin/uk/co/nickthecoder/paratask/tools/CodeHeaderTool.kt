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
import uk.co.nickthecoder.paratask.ToolParser
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.table.BaseFileColumn
import uk.co.nickthecoder.paratask.table.BooleanColumn
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.WrappedFile
import java.io.File

class CodeHeaderTool : AbstractTableTool<CodeHeaderTool.ProcessedFile>() {

    override val taskD = TaskDescription("codeHeader", description = "Adds a Copyright notice to the top of source code file")

    val directoryP = FileParameter("directory", expectFile = false, mustExist = true)

    val extensionsP = MultipleParameter("extensions", value = listOf("java", "kt")) { StringParameter("") }

    val containsP = StringParameter("contains",
            value = "This program is free software",
            description = "Check if the file already has a header by looking for this text")

    val withinLinesP = IntParameter("withinLines", description = "Within this many lines", value = 10)

    val depthP = IntParameter("depth", description = "Depth of recusrive search", value = 10)

    val headerTextP = StringParameter("headerText", rows = 20, columns = 50, value =
    """/*
<PROGRAM NAME AND DESCRIPTION>
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
*/

""")

    private lateinit var containsText: String

    init {
        taskD.addParameters(directoryP, extensionsP, depthP, containsP, withinLinesP, headerTextP)
    }

    override fun createColumns() {
        columns.add(BooleanColumn<ProcessedFile>("processed") { it.processed })
        columns.add(Column<ProcessedFile, String>("name") { it.file.name })
        columns.add(BaseFileColumn<ProcessedFile>("path", base = directoryP.value!!) { it.file })
    }

    override fun run() {

        if (list.isEmpty()) {
            containsText = containsP.value
            val lister = FileLister(/*extensions = extensionsP.value, */depth = depthP.value!!)
            list.addAll(lister.listFiles(directoryP.value!!).map { ProcessedFile(it) })
        }
    }

    fun isProcessed(file: File): Boolean {
        println("Checking ${file}")

        val reader = file.bufferedReader()
        try {
            for (i in 1..withinLinesP.value!!) {
                val line = reader.readLine()
                if (line == null) return false
                if (line.contains(containsText)) {
                    return true
                }
            }
        } finally {
            reader.close()
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
                val contents = file.readText()
                file.writeText(headerTextP.value + contents)
                processed = true
            }
        }
    }
}


fun main(args: Array<String>) {
    ToolParser(CodeHeaderTool()).go(args)
}
