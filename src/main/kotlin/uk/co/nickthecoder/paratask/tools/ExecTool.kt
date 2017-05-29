package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.AbstractTool
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.project.Results
import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.util.process.Exec

class ExecTool(val exec: Exec) : AbstractTool(), Stoppable {

    override val taskD = TaskDescription("exec")

    override fun createResults(): List<Results> {
        return singleResults(TerminalResults(this, exec))
    }

    override fun run() {
        exec.waitFor()
    }

    override fun stop() {
        exec.process?.destroy()
    }
}
