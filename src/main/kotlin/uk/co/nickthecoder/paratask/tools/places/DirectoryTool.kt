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

import javafx.scene.Node
import uk.co.nickthecoder.paratask.gui.DropFiles
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.util.AutoRefreshTool

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

    override fun detaching() {
        super<AutoRefreshTool>.detaching()
        super<AbstractDirectoryTool>.detaching()
    }

}

fun main(args: Array<String>) {
    uk.co.nickthecoder.paratask.TaskParser(DirectoryTool()).go(args)
}
