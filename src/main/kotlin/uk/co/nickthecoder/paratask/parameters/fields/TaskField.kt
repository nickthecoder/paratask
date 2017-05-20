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

    val converter = object : StringConverter<Task?>() {

        override fun fromString(label: String): Task? {
            for (task in parameter.tasks) {
                if (label == task.taskD.name) {
                    return task
                }
            }
            return null
        }

        override fun toString(obj: Task?): String {
            @Suppress("UNCHECKED_CAST")
            return obj?.taskD?.name ?: ""
        }
    }

    val button = Button("Parameters")

    init {
        comboBox.converter = converter
        comboBox.valueProperty().bindBidirectional(parameter.valueProperty)

        for (task in parameter.tasks) {
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
