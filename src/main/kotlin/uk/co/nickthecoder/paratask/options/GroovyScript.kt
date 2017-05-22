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


import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

import groovy.lang.Binding
import groovy.lang.GroovyShell
import groovy.lang.Script

class GroovyScript(val source: String) {

    private fun createShell(): GroovyShell {
        val importCustomizer = ImportCustomizer()
        importCustomizer.addStarImports(
                "uk.co.nickthecoder.paratask",
                "uk.co.nickthecoder.paratask.parameters",
                "uk.co.nickthecoder.paratask.options",
                "uk.co.nickthecoder.paratask.tools",
                "uk.co.nickthecoder.paratask.tools.git",
                "uk.co.nickthecoder.paratask.tools.places",
                "uk.co.nickthecoder.paratask.tools.editor",
                "uk.co.nickthecoder.paratask.util")

        val configuration = CompilerConfiguration()
        configuration.addCompilationCustomizers(importCustomizer)

        return GroovyShell(configuration)
    }

    val script: Script by lazy {
        createShell().parse(source)
    }

    fun run(binding: Binding): Any? {
        script.binding = binding

        try {
            return script.run()
        } catch (e: Exception) {
            throw ScriptException(this, e)
        }
    }

    override fun toString() = "GroovyScript : $source"
}