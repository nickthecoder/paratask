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

import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.nameWithoutExtension
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class SaveProjectTask(val projectWindow: ProjectWindow) : AbstractTask() {
    override val taskD = TaskDescription("Save Project")

    val saveInDirectoryP = FileParameter("saveInDirectory", mustExist = null, expectFile = false)

    val filenameP = StringParameter("filename")

    init {
        val project = projectWindow.project

        taskD.addParameters(saveInDirectoryP, filenameP)
        saveInDirectoryP.value = project.projectFile?.parentFile ?: Preferences.projectsDirectory
        filenameP.value = project.projectFile?.nameWithoutExtension() ?: ""
    }

    override fun run() {
        var filename = filenameP.value!!
        if ( filename.endsWith(".json")) {
            filename.substring(0..filename.length-5)
        }
        val file = File(saveInDirectoryP.value!!, filenameP.value + ".json")

        projectWindow.project.save(file)
    }

}
