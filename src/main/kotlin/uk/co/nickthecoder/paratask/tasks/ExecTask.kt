package uk.co.nickthecoder.paratask.tasks

import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.parameter.ChoiceParameter
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.parameter.enumChoices
import uk.co.nickthecoder.paratask.util.Command
import uk.co.nickthecoder.paratask.util.Duration
import uk.co.nickthecoder.paratask.util.Exec
import java.io.InputStream

abstract class ExecTask() : SimpleTask() {

    val outputP = ChoiceParameter<Output>("output", value = Output.PASSTHROUGH,
            description = "Where should the output of the command go?")
            .enumChoices<Output>()

    abstract fun command(values: Values): Command

    override fun run(values: Values): Command? {

        val cmd = command(values)

        if (outputP.value(values) == Output.PASSTHROUGH) {
            Exec(cmd).inheritOut().inheritErr().start()
            return null
        } else {
            return cmd
        }
    }

}