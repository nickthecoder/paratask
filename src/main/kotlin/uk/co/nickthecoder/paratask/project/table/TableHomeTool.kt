package uk.co.nickthecoder.paratask.project.table

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.AbstractTool
import uk.co.nickthecoder.paratask.project.task.HomeTool

class TableHomeTool : AbstractTool() {

    override val taskD = TaskDescription("home", description="Lists available Tools")

    override fun run(value: Values) {
    }

    override fun updateResults() {
        toolPane?.updateResults(TableHomeResults(HomeTool.toolList))
    }
}