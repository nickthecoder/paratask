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
import uk.co.nickthecoder.paratask.misc.AutoRefresh

class DirectoryTool() :
        AbstractDirectoryTool("directory", "Work with a Single Directory") {

    val autoRefreshP = uk.co.nickthecoder.paratask.parameters.BooleanParameter("autoRefresh", value = true,
            description = "Refresh the list when the contents of the directory changes")

    val autoRefresh = AutoRefresh { toolPane?.parametersPane?.runIfNotAlreadyRunning() }

    init {
        depthP.hidden = true
        taskD.addParameters(autoRefreshP)
    }

    constructor(file: java.io.File) : this() {
        directoriesP.value = listOf(file)
    }

    override fun run() {
        super.run()
        autoRefresh.unwatchAll()
        if (autoRefreshP.value == true) {
            directoriesP.value.filterNotNull().forEach {
                autoRefresh.watch(it)
            }
        }
    }

    override fun detaching() {
        autoRefresh.unwatchAll()
        super<AbstractDirectoryTool>.detaching()
    }

}

fun main(args: Array<String>) {
    TaskParser(DirectoryTool()).go(args)
}
