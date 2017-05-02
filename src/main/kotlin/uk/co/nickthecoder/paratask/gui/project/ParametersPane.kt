package uk.co.nickthecoder.paratask.gui.project

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import uk.co.nickthecoder.paratask.gui.FocusListener
import uk.co.nickthecoder.paratask.gui.field.TaskForm
import uk.co.nickthecoder.paratask.project.Tool

interface ParametersPane {

    val tool: Tool

    val taskForm: TaskForm

    fun run() : Boolean

    fun attached(toolPane: ToolPane)

    fun detaching()
}
