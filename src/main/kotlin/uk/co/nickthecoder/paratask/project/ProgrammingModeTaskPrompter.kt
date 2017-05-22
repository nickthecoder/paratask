package uk.co.nickthecoder.paratask.project

import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.Task

open class ProgrammingModeTaskPrompter(task: Task) : AbstractTaskPrompter(task) {


    val doneButton = Button("Done")

    override fun build() {
        super.build()

        with(doneButton) {
            visibleProperty().bind(task.taskRunner.showRunProperty)
            disableProperty().bind(task.taskRunner.disableRunProperty)
            defaultButtonProperty().set(true)
            onAction = javafx.event.EventHandler {
                onDone()
            }
        }
        buttons.children.add(doneButton)
    }

    fun onDone() {
        close()
    }

}
