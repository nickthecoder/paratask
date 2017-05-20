package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.table.AbstractTableTool
import uk.co.nickthecoder.paratask.util.process.BufferedSink
import uk.co.nickthecoder.paratask.util.process.OSCommand
import uk.co.nickthecoder.paratask.util.process.Exec

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

    abstract fun createCommand(): OSCommand
}