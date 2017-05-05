package uk.co.nickthecoder.paratask.project.option


import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.customizers.ImportCustomizer

import groovy.lang.Binding
import groovy.lang.GroovyShell
import groovy.lang.Script

class GroovyScript(val source: String) {

    private fun createShell(): GroovyShell {
        val importCustomizer = ImportCustomizer()
        importCustomizer.addStarImports(
                "uk.co.nickthecoder.wrkfoo",
                "uk.co.nickthecoder.wrkfoo.tool",
                "uk.co.nickthecoder.wrkfoo.util",
                "uk.co.nickthecoder.wrkfoo.editor",
                "uk.co.nickthecoder.jguifier",
                "uk.co.nickthecoder.jguifier.util")

        val configuration = CompilerConfiguration()
        configuration.addCompilationCustomizers(importCustomizer)

        return GroovyShell(configuration)
    }

    val script: Script by lazy {
        createShell().parse(source)
    }

    public fun run(binding: Binding): Any? {
        script.setBinding(binding);

        try {
            return script.run()
        } catch (e: Exception) {
            throw ScriptException(this, e)
        }
    }
}