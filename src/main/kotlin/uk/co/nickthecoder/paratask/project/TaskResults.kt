package uk.co.nickthecoder.paratask.project

import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.parameters.fields.ParametersForm
import uk.co.nickthecoder.paratask.util.focusNext

class TaskResults(tool: Tool, val task: Task) : AbstractResults(tool, "Meta Data") {

    val prompter = ResultsTaskPrompter()

    override val node = prompter.root

    override fun focus() {
        node.focusNext()
    }

    init {
        prompter.build()
    }

    inner class ResultsTaskPrompter : AbstractTaskPrompter(task) {

        override fun build() {
            super.build()

            with(okButton) {
                visibleProperty().bind(task.taskRunner.showRunProperty)
                disableProperty().bind(task.taskRunner.disableRunProperty)
                defaultButtonProperty().set(true)
                onAction = javafx.event.EventHandler {
                    onOk()
                }
            }
            buttons.children.addAll(okButton)
        }

        val okButton = Button("OK")

        fun onOk() {
            task.taskRunner.run()
        }
    }
}