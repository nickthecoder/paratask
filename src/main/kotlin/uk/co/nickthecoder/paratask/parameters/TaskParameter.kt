package uk.co.nickthecoder.paratask.parameters

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.fields.LabelledField
import uk.co.nickthecoder.paratask.parameters.fields.TaskField
import uk.co.nickthecoder.paratask.util.uncamel

/**
 * Allows a Task to be chosen from a list of tasks, and shows a button, which when pressed, allows
 * the tasks' parameters to be filled in by the user.
 * This is used when editing TaskOptions as part of the OptionsTool
 */
class TaskParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: Task? = null,
        required: Boolean = true,
        val tasks: List<Task> = listOf<Task>())

    : AbstractValueParameter<Task?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

    override val converter = object : StringConverter<Task?>() {
        override fun fromString(str: String): Task? {
            return tasks.firstOrNull { it.taskD.name == str }
        }

        override fun toString(task: Task?): String {
            return task?.taskD?.name ?: ""
        }
    }

    override fun errorMessage(v: Task?): String? = null

    override fun isStretchy() = false

    override fun createField(): LabelledField = TaskField(this)

    override fun toString(): String = "Int" + super.toString()

}

