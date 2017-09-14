package uk.co.nickthecoder.paratask.project

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.TaskPrompter


abstract class AbstractTaskButton(val task: Task, val label: String, val icon: Image?) : Button() {
    init {
        onAction = EventHandler { onAction() }

        icon?.let { graphic = ImageView(icon) }
        if (icon == null && label.isBlank()) {
            text = task.taskD.label
        } else {
            text = label
        }
    }

    abstract fun onAction()

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
    : AbstractTaskButton(task, label, icon) {

    override fun onAction() {
        val taskPrompter = TaskPrompter(task)
        taskPrompter.build()
        taskPrompter.placeOnStage(Stage())
    }

}


open class ToolButton(val projectWindow: ProjectWindow, val tool: Tool, label: String, icon: Image?, val newTab: Boolean)
    : AbstractTaskButton(tool, label, icon) {

    override fun onAction() {
        val t = if (newTab) tool.copy() else tool

        t.toolPane?.halfTab?.let { halfTab ->
            halfTab.projectTab.isSelected = true
            halfTab.toolPane.focusResults()
            return
        }
        projectWindow.addTool(t)
    }

}
