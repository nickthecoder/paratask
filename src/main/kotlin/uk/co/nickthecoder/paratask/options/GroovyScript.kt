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