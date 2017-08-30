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

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class KotlinScript(val source: String) {

    private fun createEngine(): ScriptEngine {
        return ScriptEngineManager().getEngineByExtension("kts")!!
    }

    fun run(bindings: Map<String, Any?>): Any? {
        val engine = createEngine()

        bindings.forEach { key, value ->
            engine.put(key, value)
        }

        val wholeSource = source
        /*"""
import uk.co.nickthecoder.paratask",uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.options.*
import uk.co.nickthecoder.paratask.tools.*
import uk.co.nickthecoder.paratask.tools.git.*
import uk.co.nickthecoder.paratask.tools.places.*
import uk.co.nickthecoder.paratask.tools.editor.*
import uk.co.nickthecoder.paratask.util.*
import uk.co.nickthecoder.paratask.util.process.*

${source}
"""
*/
        try {
            return engine.eval(wholeSource)
        } catch (e: Exception) {
            throw ScriptException(source, e)
        }
    }

    override fun toString() = "KotlinScript : $source"
}
