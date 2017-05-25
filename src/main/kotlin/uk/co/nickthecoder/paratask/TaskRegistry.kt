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

package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.tools.editor.EditorTool
import uk.co.nickthecoder.paratask.tools.OSCommandTask
import uk.co.nickthecoder.paratask.tools.DirectoryTool
import uk.co.nickthecoder.paratask.tools.DirectoryTreeTool
import uk.co.nickthecoder.paratask.tools.GrepTool
import uk.co.nickthecoder.paratask.tools.GroovyTool
import uk.co.nickthecoder.paratask.tools.HomeTool
import uk.co.nickthecoder.paratask.tools.OptionsFilesTool
import uk.co.nickthecoder.paratask.tools.places.PlacesTool
import uk.co.nickthecoder.paratask.tools.PythonTool
import uk.co.nickthecoder.paratask.tools.TerminalTool
import uk.co.nickthecoder.paratask.tools.WebTool
import uk.co.nickthecoder.paratask.tools.git.*
import uk.co.nickthecoder.paratask.tools.places.PlacesDirectoryTool

object TaskRegistry {

    private val taskGroups = mutableListOf<TaskGroup>()

    val home = TaskGroup("Home")

    val topLevel = TaskGroup("Top Level")

    init {
        home.addTools(
                HomeTool(),
                DirectoryTool(), DirectoryTreeTool(), PlacesTool(), PlacesDirectoryTool(),
                TerminalTool(), PythonTool(), GroovyTool(),
                WebTool(), EditorTool(),
                GrepTool(), GitTool(),
                OptionsFilesTool()
        )

        topLevel.addTools(TerminalTool())
        topLevel.addTasks(OSCommandTask())

        val git = TaskGroup("Git")
        git.addTools(GitTool(), GitLogTool(), GitCommittedFilesTool())
        git.addTasks(GitCommitTask(), GitRMTask())

        addGroup(topLevel)
        addGroup(home)
        addGroup(git)
    }

    fun listGroups(): List<TaskGroup> = taskGroups

    fun addGroup(group: TaskGroup) {
        taskGroups.add(group)
    }

}
