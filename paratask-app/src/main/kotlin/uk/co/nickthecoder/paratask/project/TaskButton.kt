package uk.co.nickthecoder.paratask.project

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.TransferMode
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.DropFiles
import uk.co.nickthecoder.paratask.gui.TaskPrompter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import java.io.File


abstract class AbstractTaskButton(val task: Task, val label: String, val icon: Image?, val copyTask: Boolean) : Button() {
    init {
        onAction = EventHandler { onAction() }

        icon?.let { graphic = ImageView(icon) }
        if (icon == null && label.isBlank()) {
            text = task.taskD.label
        } else {
            text = label
        }
    }

    init {
        val unnamed = task.taskD.unnamedParameter
        if (unnamed is FileParameter || unnamed is MultipleParameter<*> && unnamed.factory() is FileParameter) {
            DropFiles(arrayOf(TransferMode.LINK)) { _, files -> onDroppedFiles(files) }.applyTo(this)
        }
    }

    fun onDroppedFiles(files: List<File>) {
        val t = createTask()

        val unamed = t.taskD.unnamedParameter
        if (unamed is FileParameter) {
            unamed.value = files.firstOrNull()
        } else if (unamed is MultipleParameter<*> && unamed.factory() is FileParameter) {
            if (copyTask) {
                println("Clearing unamed parameter values")
                unamed.clear()
            }
            files.forEach { file ->
                val fp = unamed.newValue()
                if (fp is FileParameter) {
                    fp.value = file
                }
            }
        }
        runTask(t)
        if (t is Tool && !copyTask) {
            t.toolPane?.parametersPane?.run()
        }
    }

    fun onAction() {
        runTask(createTask())
    }

    fun createTask() = if (copyTask) task.copy() else task

    abstract fun runTask(task: Task)

    companion object {

        fun createToolOrTaskButton(
                projectWindow: ProjectWindow,
                task: Task, label: String = task.taskD.label,
                icon: Image? = null,
                newTab: Boolean = true): Button {

            if (task is Tool) {
                return ToolButton(projectWindow, task, label, icon ?: task.icon, newTab)
            } else {
                return TaskButton(task, label, icon)
            }
        }

    }

}


class TaskButton(task: Task, label: String, icon: Image?)
    : AbstractTaskButton(task, label, icon, copyTask = true) {

    override fun runTask(task: Task) {
        val taskPrompter = TaskPrompter(task)
        taskPrompter.placeOnStage(Stage())
    }
}


open class ToolButton(val projectWindow: ProjectWindow, val tool: Tool, label: String, icon: Image?, copyTask: Boolean)
    : AbstractTaskButton(tool, label, icon, copyTask) {

    override fun runTask(task: Task) {
        if (task is Tool) {
            task.toolPane?.halfTab?.let { halfTab ->
                halfTab.projectTab.isSelected = true
                halfTab.toolPane.focusResults()
                return
            }
            projectWindow.addTool(task)
        }
    }

}
