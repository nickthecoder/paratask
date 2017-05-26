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
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.util.FileLister
import java.io.File

/**
 * Note, unlike more Tasks, this CANNOT be run from the command line, use StartTask instead. This is because it
 * does not attempt to start JavaFX - it assumes it is already running.
 */
class OpenProjectTask : AbstractTask() {
    override val taskD = TaskDescription("Open Project")

    val directory = FileParameter(
            "Projects Directory",
            mustExist = true,
            expectFile = false,
            value = Preferences.projectsDirectory)

    val name = ChoiceParameter<String?>("name", value = null)

    init {
        taskD.addParameters(directory, name)
        updateNameChoices()
        directory.listen { updateNameChoices() }
    }

    fun updateNameChoices() {
        name.clear()
        val dir = directory.value ?: return

        val lister = FileLister(extensions = listOf("json"))
        for (file in lister.listFiles(dir)) {
            val label = file.nameWithoutExtension
            name.choice(label, label, label)
        }
    }

    override fun run() {
        val file = File(directory.value, name.value + ".json")
        Platform.runLater {
            Project.load(file).projectWindow.placeOnStage(Stage())
        }
    }

}
