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

package uk.co.nickthecoder.paratask.tools.places

import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.project.HeaderRow
import uk.co.nickthecoder.paratask.project.HeaderRows
import java.io.File

class DirectoryTreeTool() : AbstractDirectoryTool("directoryTree", "Work with a Directory Tree") {

    override val optionsName = "directory"

    init {
        depthP.value = 3
    }

    constructor(directory: File) : this() {
        this.directoriesP.value = listOf(directory)
    }

    override fun createHeaderRows(dirP: FileParameter): HeaderRows = HeaderRows(this, listOf(HeaderRow().addAll(dirP, depthP)))

    override fun isTree() = depthP.value != 1
}

fun main(args: Array<String>) {
    TaskParser(DirectoryTreeTool()).go(args)
}
