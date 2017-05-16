package uk.co.nickthecoder.paratask.project.task

import uk.co.nickthecoder.paratask.project.Stoppable
import uk.co.nickthecoder.paratask.project.table.AbstractTableTool
import uk.co.nickthecoder.paratask.util.BufferedSink
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Exec

abstract class AbstractCommandTool<T :Any>() : AbstractTableTool<T>(), Stoppable {

    protected var exec: Exec? = null

    open override fun run() {

        list.clear()

        val exec = Exec(createCommand())
        exec.outSink = BufferedSink { processLine(it) }
        exec.start().waitFor()
    }

    override fun stop() {
        exec?.kill()
    }

    abstract fun processLine(line: String)

    abstract fun createCommand(): Command
}