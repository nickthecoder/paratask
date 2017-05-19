package uk.co.nickthecoder.paratask.parameter

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.CopyableTask
import uk.co.nickthecoder.paratask.gui.field.LabelledField
import uk.co.nickthecoder.paratask.gui.field.TaskField
import uk.co.nickthecoder.paratask.project.task.DirectoryTool
import uk.co.nickthecoder.paratask.project.task.GrepTool
import uk.co.nickthecoder.paratask.project.task.HomeTool
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
        value: CopyableTask? = null,
        required: Boolean = true,
        val tasks: List<CopyableTask> = listOf<CopyableTask>())

    : AbstractValueParameter<CopyableTask?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

    override val converter = object : StringConverter<CopyableTask?>() {
        override fun fromString(str: String): CopyableTask? {
            for (task in tasks) {
                if (task.taskD.name == str) {
                    return task
                }
            }
            return null
        }

        override fun toString(task: CopyableTask?): String {
            return task?.taskD?.name ?: ""
        }
    }

    override fun errorMessage(v: CopyableTask?): String? {
        return null
    }

    override fun isStretchy() = false

    override fun createField(): LabelledField = TaskField(this)

    override fun toString(): String = "Int" + super.toString()

}

