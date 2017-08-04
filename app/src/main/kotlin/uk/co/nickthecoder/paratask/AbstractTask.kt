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

package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.project.TaskRunner
import uk.co.nickthecoder.paratask.project.ThreadedTaskRunner
import uk.co.nickthecoder.paratask.util.currentDirectory

abstract class AbstractTask : Task {

    override val taskRunner: TaskRunner by lazy { ThreadedTaskRunner(this) }

    override var resolver: ParameterResolver = PlainDirectoryResolver()

    override fun check() {
        taskD.root.check()
        customCheck()
    }

    override fun customCheck() {}

    override fun toString(): String {
        return "Task : $taskD"
    }
}