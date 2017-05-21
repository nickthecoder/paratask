package uk.co.nickthecoder.paratask.parameters.fields

import javafx.scene.Node
import javafx.scene.control.ScrollPane
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.ValueParameter

class TaskForm(val task: Task) {

    val form = ParametersForm(task.taskD.root)

    val scrollPane = ScrollPane(form)

    init {
        scrollPane.fitToWidthProperty().set(true)
        form.buildContent()
    }

    fun check(): Boolean {

        val programming = task.taskD.programmingMode

        // Are there any "dirty" fields, where the value in the GUI isn't in the Value.
        // For example, if a non-valid number is typed into an IntField
        form.descendants().forEach { field ->
            val parameter = field.parameter
            if (!programming || (parameter is ValueParameter<*> && parameter.expression != null)) {
                if (field.isDirty()) {
                    ensureVisible(field)
                    return false
                }
            }
        }

        form.descendants().forEach { field ->
            field.clearError()
        }

        try {
            task.taskD.root.check()
            task.check()

        } catch (e: ParameterException) {
            val field = form.findField(e.parameter)
            if (field != null) {
                field.showError(e.message)
                ensureVisible(field)
            }

            return false
        }
        return true
    }

    private fun ensureVisible(node: Node) {
        // TODO Ensure all the field's parents are not collapsed

        // Ensures the field is visible within the scrollPane
        val viewport = scrollPane.viewportBounds
        val contentHeight = scrollPane.content.boundsInLocal.height

        val bounds = scrollPane.content.sceneToLocal(node.localToScene(node.boundsInLocal))

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