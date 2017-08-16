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

import groovy.lang.Binding
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.misc.ThreadedDesktop
import java.io.Serializable

class GroovyOption(var script: String)
    : AbstractOption(), Serializable {

    /**
     * For serialization
     */
    constructor() : this("")

    private var gscript: GroovyScript? = null

    fun getScript(): GroovyScript {
        val gs = gscript
        if (gs == null || gs.source !== script) {
            gscript = GroovyScript(script)
        }
        return gscript!!
    }

    override fun run(tool: Tool, row: Any): Any? {
        return runScript(getScript(), tool, isMultiple, row)
    }

    override fun runMultiple(tool: Tool, rows: List<Any>): Any? {
        return runScript(getScript(), tool, isMultiple, rows)
    }

    override fun runNonRow(tool: Tool): Any? {
        return runScript(getScript(), tool, false, null)
    }

    private fun runScript(gscript: GroovyScript, tool: Tool, isMultiple: Boolean, rowOrRows: Any?): Any? {

        val bindings = Binding()
        bindings.setProperty("tool", tool)
        bindings.setProperty("desktop", ThreadedDesktop.instance)
        bindings.setProperty("helper", Helper.instance)

        if (rowOrRows != null) {
            bindings.setProperty(if (isMultiple) "rows" else "row", rowOrRows)
        }

        return gscript.run(bindings)
    }

    override fun copy(): GroovyOption {
        val result = GroovyOption(script)
        copyTo(result)
        return result
    }
}
