package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.IntParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.ValueParameter

class Example : Task("Example") {

    val oneToTen = IntParameter("oneToTenRequired", range = 1..10)
    val fromOne = IntParameter("fromOneOptional", range = 1..Int.MAX_VALUE, required = false)
    val greeting = StringParameter("Greeting")

    init {
        addParameters(oneToTen, fromOne, greeting)
    }

    override fun run() {
        println("Example Parameter values : ")

        // TODO Iteratate over all parameters, including those in a Group (when that feature has been implemented)
        root.forEach {
            if (it is ValueParameter<*>) {
                println("Parameter ${it.name} = ${it.value} ('${it.getStringValue()}')")
            }
        }
    }
}

fun main(args: Array<String>) {
    CommandTask(Example()).go(args)
}