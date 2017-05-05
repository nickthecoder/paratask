package uk.co.nickthecoder.paratask.project.option

import com.sun.javafx.tk.Toolkit.Task
import groovy.lang.Binding
import uk.co.nickthecoder.paratask.gui.SimpleTerminal
import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.util.Command

data class GroovyOption(
        override val code: String,
        override val label: String,
        override var isRow: Boolean = true,
        override var isMultiple: Boolean = false,
        override var newTab: Boolean = false,
        override var prompt: Boolean = false,
        override var refresh: Boolean = false,
        var script: String)

    : Option {

    val gscript = GroovyScript(script)

    override fun run(tool: Tool, row: Any): Any? {
        return runScript(gscript, tool, isMultiple, row)
    }

    override fun runNonRow(tool: Tool): Any? {
        return runScript(gscript, tool, false, null)
    }

    private fun runScript(gscript: GroovyScript, tool: Tool, isMultiple: Boolean, rowOrRows: Any?): Any? {

        val bindings = Binding()
        bindings.setProperty("tool", tool)
        // TODO Add helper functions
        //bindings.setProperty("os", OSHelper.instance)

        if (rowOrRows != null) {
            bindings.setProperty(if (isMultiple) "rows" else "row", rowOrRows)
        }

        return gscript.run(bindings)
    }

}
