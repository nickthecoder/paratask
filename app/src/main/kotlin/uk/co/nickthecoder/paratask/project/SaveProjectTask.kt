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

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter

class SaveProjectTask(val project: Project) : AbstractTask() {

    override val taskD = TaskDescription("Save Project")

    val fileP = FileParameter("projectFile", mustExist = null, expectFile = true, extensions = listOf<String>("json"))

    val projectDataP = project.projectDataP

    init {
        taskD.addParameters(fileP, projectDataP)
        fileP.value = project.projectFile
    }

    override fun run() {
        project.projectWindow.project.save(fileP.value!!)
    }

}
