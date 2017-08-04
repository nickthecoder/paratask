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

package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.util.process.BufferedSink
import uk.co.nickthecoder.paratask.util.process.OSCommand
import uk.co.nickthecoder.paratask.util.process.Exec

abstract class AbstractCommandTool<T :Any> : AbstractTableTool<T>(), Stoppable {

    protected var exec: Exec? = null

    override fun run() {

        list.clear()

        val exec = Exec(createCommand())
        exec.outSink = BufferedSink { processLine(it) }
        exec.start().waitFor()
    }

    override fun stop() {
        exec?.kill()
    }

    abstract fun processLine(line: String)

    abstract fun createCommand(): OSCommand
}