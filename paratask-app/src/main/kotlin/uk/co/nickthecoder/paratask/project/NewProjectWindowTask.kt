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

package uk.co.nickthecoder.paratask.project

import javafx.application.Platform
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter

/**
 * Used to open a new project window from an *existing* project.
 * Note, unlike most Tasks, this CANNOT be run from the command line. This is because it
 * does not attempt to start JavaFX - it assumes it is already running.
 * See OpenProjectTask for loading a project from the command line.
 */
class NewProjectWindowTask : AbstractTask() {
    override val taskD = TaskDescription("Open Project")

    val fileP = FileParameter(
            "projectFile",
            mustExist = true,
            expectFile = true,
            value = Preferences.projectsDirectory)

    init {
        taskD.addParameters(fileP)
    }

    override fun run() {
        Platform.runLater {
            Project.load(fileP.value!!)
        }
    }

}
