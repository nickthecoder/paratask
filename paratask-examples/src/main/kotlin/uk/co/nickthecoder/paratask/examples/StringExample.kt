package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.StringParameter

class StringExample : AbstractTask() {

    val requiredP = StringParameter("required", hint = "This one is required")
    var required by requiredP

    val optionalP = StringParameter("optional", hint = "This one is optional", required = false)
    val optional by optionalP

    override val taskD = TaskDescription("stringExample")
            .addParameters(requiredP, optionalP)


    override fun run() {
        println("Required=$required, Optional=$optional")
    }

}

fun main(args: Array<String>) {
    TaskParser(StringExample()).go(args, prompt = true)
}
