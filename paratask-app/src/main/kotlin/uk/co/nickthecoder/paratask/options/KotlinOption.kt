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

package uk.co.nickthecoder.paratask.options

import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.misc.ThreadedDesktop
import java.io.Serializable

class KotlinOption(var script: String)
    : AbstractOption(), Serializable {

    /**
     * For serialization
     */
    constructor() : this("")


    override fun run(tool: Tool, row: Any): Any? {
        return runScript(tool, isMultiple, row)
    }

    override fun runMultiple(tool: Tool, rows: List<Any>): Any? {
        return runScript(tool, isMultiple, rows)
    }

    override fun runNonRow(tool: Tool): Any? {
        return runScript(tool, false, null)
    }

    private fun runScript(tool: Tool, isMultiple: Boolean, rowOrRows: Any?): Any? {

        val bindings = mutableMapOf<String, Any?>()
        bindings["tool"] = tool
        bindings["desktop"] = ThreadedDesktop.instance
        bindings["helper"] = Helper.instance

        if (rowOrRows != null) {
            bindings[if (isMultiple) "rows" else "row"] = rowOrRows
        }

        return KotlinScript(script).run(bindings)
    }

    override fun copy(): KotlinOption {
        val result = KotlinOption(script)
        copyTo(result)
        return result
    }

}
