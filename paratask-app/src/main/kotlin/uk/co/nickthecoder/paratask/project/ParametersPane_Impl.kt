package uk.co.nickthecoder.paratask.project

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.gui.defaultWhileFocusWithin
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm
import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.util.focusNext


open class ParametersPane_Impl(override val task: Task)

    : ParametersPane, BorderPane() {

    override val taskForm = TaskForm(task)

    protected val buttons = FlowPane()

    val runButton = Button("Run")

    protected val stopButton = Button("Stop")

    protected lateinit var toolPane: ToolPane

    init {
        center = taskForm.scrollPane
        bottom = buttons

        stopButton.onAction = EventHandler { onStop() }
        runButton.onAction = EventHandler { onRun() }

        val runStop = StackPane()
        runStop.children.addAll(stopButton, runButton)

        stopButton.visibleProperty().bind(task.taskRunner.showStopProperty)
        runButton.visibleProperty().bind(task.taskRunner.showRunProperty)
        runButton.disableProperty().bind(task.taskRunner.disableRunProperty)

        buttons.children.addAll(runStop)
        buttons.styleClass.add("buttons")
    }

    override fun run(): Boolean {

        task.resolveParameters(toolPane.halfTab.projectTab.projectTabs.projectWindow.project.resolver)

        if (taskForm.check()) {

            //toolPane.halfTab.pushHistory(tool)

            task.taskRunner.run()

            return true
        }
        return false
    }

    override fun runIfNotAlreadyRunning(): Boolean {
        if (!task.taskRunner.isRunning()) {
            return run()
        }
        return false
    }

    private fun onStop() {
        if (task is Stoppable) {
            (task as Stoppable).stop()
        }
    }

    private fun onRun() {
        run()
    }

    override fun attached(toolPane: ToolPane) {
        this.toolPane = toolPane
        runButton.defaultWhileFocusWithin(this, "ParametersPane Run")
    }

    override fun detaching() {
    }

    override fun focus() {
        ParaTaskApp.logFocus("ParametersPane_Impl focus. focusNext()")
        this.focusNext()
    }

}
