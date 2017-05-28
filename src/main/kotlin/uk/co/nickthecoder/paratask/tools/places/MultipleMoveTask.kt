package uk.co.nickthecoder.paratask.tools.places

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.paratask.util.process.OSCommand

class MultipleMoveTask() : AbstractTask() {

    override val taskD = TaskDescription("multipleMove", description =
"""Moves multiple files using wilcard patterns.

Examples
    Rename all *.jpeg files to *.jpg:
          from=*.jpeg  to=#1.jpg

    Replace the first occurrence of abc with xyz
          from=*abc* to=#1xyz#2
""")

    val directoryP = FileParameter("toDirectory", mustExist = true, expectFile = false)

    val fromP = StringParameter("from")

    val toP = StringParameter("to")

    init {
        taskD.addParameters(directoryP, fromP, toP)
    }

    override fun run() {
        val command = OSCommand("mmv", "--", fromP.value, toP.value).dir(directoryP.value!!)
        Exec(command).start().waitFor()
    }
}
