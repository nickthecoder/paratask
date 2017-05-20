package uk.co.nickthecoder.paratask.options

import groovy.lang.Binding
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.util.ThreadedDesktop

class GroovyOption(var script: String)
    : AbstractOption() {

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
