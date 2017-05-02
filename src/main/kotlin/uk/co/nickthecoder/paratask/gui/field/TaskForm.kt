package uk.co.nickthecoder.paratask.gui.field

import javafx.scene.Node
import javafx.scene.control.ScrollPane
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameter.Values

class TaskForm(val task: Task, val values: Values = task.taskD.createValues()) {

    val form = GroupParametersForm(task.taskD.root, values)

    val scrollPane = ScrollPane(form)

    init {
        scrollPane.fitToWidthProperty().set(true)
    }

    fun check(): Values? {

        // Are there any "dirty" fields, where the value in the GUI isn't in the Value.
        // For example, if a non-valid number is typed into a IntField
        form.descendants().forEach { field ->
            if (field.isDirty()) {
                ensureVisible(field)
                return null;
            }
        }

        val copiedValues = values.copy()

        form.descendants().forEach { field ->
            field.clearError()
        }

        try {
            task.taskD.root.check(copiedValues)
            task.check(copiedValues)

        } catch (e: ParameterException) {
            val field = form.findField(e.parameter)
            if (field != null) {
                field.showError(e.message!!)
                ensureVisible(field)
            }

            return null
        }
        return copiedValues
    }

    private fun ensureVisible(node: Node) {
        // TODO Ensure all the field's parents are not collapsed

        // Ensures the field is visible within the scrollPane
        val viewport = scrollPane.viewportBounds
        val contentHeight = scrollPane.content.boundsInLocal.height

        val bounds = scrollPane.content.sceneToLocal(node.localToScene(node.getBoundsInLocal()));

        val nodeMinY = bounds.minY
        val nodeMaxY = bounds.maxY

        val viewportMinY = (contentHeight - viewport.height) * scrollPane.vvalue
        val viewportMaxY = viewportMinY + viewport.height
        if (nodeMinY < viewportMinY) {
            scrollPane.vvalue = nodeMinY / (contentHeight - viewport.height)
        } else if (nodeMaxY > viewportMaxY) {
            scrollPane.vvalue = (nodeMaxY - viewport.height) / (contentHeight - viewport.height)
        }
    }
}