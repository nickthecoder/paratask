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

import uk.co.nickthecoder.paratask.ToolParser
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.util.FileListener
import uk.co.nickthecoder.paratask.util.FileWatcher
import java.io.File
import java.nio.file.Path

class DirectoryTool : AbstractDirectoryTool("directory", "Work with a Single Directory") {

    var fileListener: FileListener? = null

    val autoRefreshP = BooleanParameter("autoRefresh", value = true,
            description = "Refresh the list when the contents of the directory changes")

    init {
        depthP.hidden = true
        taskD.addParameters(autoRefreshP)
    }

    override fun run() {
        super.run()
        if (autoRefreshP.value == true) {
            watch(directoryP.value!!)
        }
    }

    fun watch(directory: File) {
        unwatch()
        fileListener = object : FileListener {
            override fun fileChanged(path: Path) {
                taskRunner.run()
            }
        }
        FileWatcher.instance.register(directory, fileListener!!)
    }

    fun unwatch() {
        fileListener?.let { FileWatcher.instance.unregister(it) }
        fileListener = null
    }

    override fun isTree(): Boolean = true

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
    }
}

fun main(args: Array<String>) {
    ToolParser(DirectoryTool()).go(args)
}
