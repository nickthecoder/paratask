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

import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.ResourceParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.project.*
import java.io.File

/**
 * The most frequently used entry point to the application. Used to open ProjectWindows from the command line.
 * If we assume a script called "paratask" sets up the classpath etc then a
 * typical command is :
 *
 * paratask project myproject myotherproject
 *
 * Which will load two project files, and open them in two separate ProjectWindows.
 * You can also prompt this command using :
 *
 * paratask project --prompt
 *
 * And then choose the projects from the gui, rather than from the command line.
 *
 */
class OpenProjectTask : AbstractTask() {

    override val taskD = TaskDescription("project")

    override var taskRunner: TaskRunner = UnthreadedTaskRunner(this)

    val projectsP = MultipleParameter("projects", minItems = 1,
            description = "The names of the projects to load. (Do not include the .json suffix)") {
        StringParameter("")
    }

    val directoryP = FileParameter(
            "directory",
            mustExist = true,
            expectFile = false,
            value = Preferences.projectsDirectory,
            description = "The directory containing the project files\ndefault=${Preferences.projectsDirectory}")

    val optionsPathP = MultipleParameter("optionsPath", minItems = 1) {
        ResourceParameter("optionsDirectory", expectFile = false)
    }

    init {
        taskD.addParameters(projectsP, directoryP, optionsPathP)

        taskD.unnamedParameter = projectsP
        optionsPathP.value = Preferences.optionsPath
    }

    override fun run() {
        Preferences.optionsPath.clear()
        Preferences.optionsPath.addAll(optionsPathP.value.filterNotNull())

        val projectFiles = projectsP.value.map {
            File(directoryP.value, it + ".json")
        }
        ParaTaskApp.openProjects(projectFiles)
    }
}


fun main(args: Array<String>) {
    TaskParser(OpenProjectTask()).go(args)
}
