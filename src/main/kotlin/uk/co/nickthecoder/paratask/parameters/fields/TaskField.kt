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

package uk.co.nickthecoder.paratask.parameters.fields

import javafx.event.ActionEvent
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.layout.HBox
import javafx.stage.Stage
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.TaskParameter
import uk.co.nickthecoder.paratask.project.ProgrammingModeTaskPrompter

class TaskField(override val parameter: TaskParameter) : LabelledField(parameter) {

    private val comboBox = ComboBox<Task>()

    val button = Button("Parameters")

    init {
        comboBox.converter = parameter.converter
        comboBox.valueProperty().bindBidirectional(parameter.valueProperty)

        for (task in parameter.creationStringToTask.values) {
            comboBox.items.add(task)
        }

        val box = HBox()
        box.children.addAll(comboBox, button)
        this.control = box

        button.addEventHandler(ActionEvent.ACTION) { onEditParameters() }
    }

    private fun onEditParameters() {
        val task = comboBox.value
        if (task != null) {
            task.taskD.programmingMode = true
            val taskPrompter = ProgrammingModeTaskPrompter(task)
            taskPrompter.placeOnStage(Stage())
        }
    }
}
