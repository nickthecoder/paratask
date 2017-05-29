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

package uk.co.nickthecoder.paratask.project

import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskListener
import uk.co.nickthecoder.paratask.util.AutoExit
import uk.co.nickthecoder.paratask.util.Stoppable

enum class RunState {
    IDLE, RUNNING, FINISHED;
}

abstract class AbstractTaskRunner(val task: Task)

    : TaskRunner {

    override var autoExit = false

    override val listeners = mutableListOf<TaskListener>()

    override val disableRunProperty = SimpleBooleanProperty(false)

    override val showRunProperty = SimpleBooleanProperty(true)

    override val showStopProperty = SimpleBooleanProperty(false)

    protected var runState: RunState = RunState.IDLE
        set(value) {
            field = value
            Platform.runLater {
                // Must only change JavaFX properties in the application thread.
                disableRunProperty.set(value == RunState.RUNNING)
                val showStop = task is Stoppable && value == RunState.RUNNING
                showStopProperty.set(showStop)
                showRunProperty.set(!showStop)
            }
        }

    override fun listen(listener: (Boolean) -> Unit) {
        listeners.add(object : TaskListener {
            override fun ended(cancelled: Boolean) {
                listener(cancelled)
            }
        })
    }

    abstract override fun run()

    open protected fun pre() {
        if (autoExit) AutoExit.inc("ThreadedTaskRunner")

        if (runState == RunState.RUNNING) {
            // As a Task has state, it cannot safely be run more than once concurrently.
            throw RuntimeException("Already running.")
        }
        task.check()
        runState = RunState.RUNNING
    }

    open protected fun post() {
        runState = RunState.FINISHED

        for (listener in listeners) {
            listener.ended(false)
        }
        if (autoExit) AutoExit.dec("ThreadedTaskRunner")

    }

    override fun cancel() {
        for (listener in listeners) {
            listener.ended(true)
        }
    }

    open protected fun runTask() {
        val result = task.run()
    }

    override fun hasStarted() = runState != RunState.IDLE

    override fun hasFinished() = runState == RunState.FINISHED

    override fun isRunning() = runState == RunState.RUNNING

}
