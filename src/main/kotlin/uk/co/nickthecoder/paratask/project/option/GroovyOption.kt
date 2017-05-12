package uk.co.nickthecoder.paratask.project.option

import groovy.lang.Binding
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.util.ThreadedDesktop

data class GroovyOption(
        override var code: String = "",
        override var aliases: MutableList<String> = mutableListOf<String>(),
        override var label: String = "",
        override var isRow: Boolean = true,
        override var isMultiple: Boolean = false,
        override var newTab: Boolean = false,
        override var prompt: Boolean = false,
        override var refresh: Boolean = false,
        override var script: String = "")

    : Option {

    private var gscript: GroovyScript? = null

    fun getScript(): GroovyScript {
        var gs = gscript
        if (gs == null || gs.source !== script) {
            gscript = GroovyScript(script)
        }
        return gscript!!
    }

    override fun run(tool: Tool, row: Any): Any? {
        return runScript(getScript(), tool, isMultiple, row)
    }

    override fun runMultiple(tool: Tool, list: List<Any>): Any? {
        return runScript(getScript(), tool, isMultiple, list)
    }

    override fun runNonRow(tool: Tool): Any? {
        return runScript(getScript(), tool, false, null)
    }

    private fun runScript(gscript: GroovyScript, tool: Tool, isMultiple: Boolean, rowOrRows: Any?): Any? {

        val bindings = Binding()
        bindings.setProperty("tool", tool)
        // TODO Add helper functions
        bindings.setProperty("desktop", ThreadedDesktop.instance)

        if (rowOrRows != null) {
            bindings.setProperty(if (isMultiple) "rows" else "row", rowOrRows)
        }

        return gscript.run(bindings)
    }

}
