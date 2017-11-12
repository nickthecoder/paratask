package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.paratask.parameters.OneOfParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter


class OneOfExample : AbstractTask() {

    val doubleP = DoubleParameter("double", value = 0.0)

    val stringP = StringParameter("string")

    val oneOfP = OneOfParameter("oneOf", value = doubleP, choiceLabel = "Choose")
            .addChoices(doubleP, stringP)

    override val taskD = TaskDescription("oneOfExample")
            .addParameters(oneOfP, doubleP, stringP)

    override fun run() {
        when (oneOfP.value) {
            doubleP -> {
                println("Double = ${doubleP.value}")
            }
            stringP -> {
                println("String = ${stringP.value}")
            }
        }

    }
}

fun main(args: Array<String>) {
    TaskParser(OneOfExample()).go(args, prompt = true)
}
