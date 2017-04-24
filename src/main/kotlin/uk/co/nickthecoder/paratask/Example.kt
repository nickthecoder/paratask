package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.IntParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.ValueParameter
import uk.co.nickthecoder.paratask.parameter.Values

class Example : SimpleTask() {

    override val taskD = TaskDescription()

    init {
        val oneToTen = IntParameter("oneToTenRequired", range = 1..10)
        val fromOne = IntParameter("fromOneOptional", range = 1..Int.MAX_VALUE, required = false)
        val greeting = StringParameter("Greeting")

        taskD.addParameters(oneToTen, fromOne, greeting)
    }

    override fun run(values: Values) {
        println("Example Parameter values : ")

        taskD.root.descendants().forEach { parameter ->
            if (parameter is ValueParameter<*>) {
                if (parameter !is GroupParameter) {
                    val value = values.get(parameter.name)
                    println("Parameter ${parameter.name} = ${value?.value} ('${value?.stringValue}')")
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    CommandTask(Example()).go(args)
}