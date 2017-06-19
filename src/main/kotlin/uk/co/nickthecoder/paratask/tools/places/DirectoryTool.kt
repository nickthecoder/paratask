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

import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.gui.DropFiles
import uk.co.nickthecoder.paratask.table.TableResults
import uk.co.nickthecoder.paratask.util.AutoRefreshTool
import uk.co.nickthecoder.paratask.util.FileOperations
import uk.co.nickthecoder.paratask.util.WrappedFile
import java.io.File

class DirectoryTool() :
        AbstractDirectoryTool("directory", "Work with a Single Directory"),
        uk.co.nickthecoder.paratask.util.AutoRefreshTool {

    val autoRefreshP = uk.co.nickthecoder.paratask.parameters.BooleanParameter("autoRefresh", value = true,
            description = "Refresh the list when the contents of the directory changes")

    init {
        depthP.hidden = true
        taskD.addParameters(autoRefreshP)
    }

    constructor(file: java.io.File) : this() {
        directoryP.value = file
    }

    override fun run() {
        super.run()
        if (autoRefreshP.value == true) {
            watch(directory!!)
        }
    }

    override fun isTree(): Boolean = true

    override fun detaching() {
        super<AutoRefreshTool>.detaching()
        super<AbstractDirectoryTool>.detaching()
    }

    override fun createTableResults(): TableResults<WrappedFile> {
        val tableResults = super.createTableResults()

        DropFiles(tableResults.tableView) { files, transferMode ->
            droppedFiles(files, transferMode)
        }

        return tableResults
    }

    fun droppedFiles(files: List<File>, transferMode: TransferMode): Boolean {
        val dir = directory
        if (dir != null) {
            if (transferMode == TransferMode.COPY) {
                FileOperations.instance.copyFiles(files, dir)
            }
            return true
        }
        return false
    }
}

fun main(args: Array<String>) {
    uk.co.nickthecoder.paratask.TaskParser(DirectoryTool()).go(args)
}
