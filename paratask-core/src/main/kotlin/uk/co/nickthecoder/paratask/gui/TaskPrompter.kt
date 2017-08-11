/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask.gui

import javafx.event.EventHandler
import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.gui.AbstractTaskPrompter

open class TaskPrompter(task: Task, val showApply: Boolean = false) : AbstractTaskPrompter(task) {

    val okButton = Button("OK")

    val cancelButton = Button("Cancel")

    val applyButton = Button("Apply")

    override fun build() {
        super.build()

        with(okButton) {
            visibleProperty().bind(task.taskRunner.showRunProperty)
            disableProperty().bind(task.taskRunner.disableRunProperty)
            defaultButtonProperty().set(true)
            onAction = EventHandler {
                onOk()
            }
        }

        with(cancelButton) {
            onAction = EventHandler { onCancel() }
            cancelButtonProperty().set(true)
        }

        with(applyButton)
        {
            onAction = EventHandler { onApply() }
            visibleProperty().bind(task.taskRunner.showRunProperty)
        }

        buttons.children.addAll(okButton, cancelButton)
        if (showApply) {
            buttons.children.add(applyButton)
        }
    }

    fun onCancel() {
        task.taskRunner.cancel()
        close()
    }

    fun onOk() {
        task.taskRunner.autoExit = true
        if (checkAndRun()) {
            close()
        }
    }

    private fun onApply() {
        checkAndRun()
    }

    fun checkAndRun(): Boolean {

        if (taskForm.check()) {
            run()
            return true
        }
        return false
    }

    open fun run() {
        task.taskRunner.run()
    }
}
