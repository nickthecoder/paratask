package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.IntParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.ValueParameter
import uk.co.nickthecoder.paratask.parameter.Values

class Example : SimpleTask() {

    override val taskD = TaskDescription()

    init {
        val greeting = StringParameter("Greeting")
        val range = GroupParameter("Range")
        val rangeFrom = IntParameter("rangeFrom", label = "From", range = 1..100, value = 1)
        val rangeTo = IntParameter("rangeTo", label = "To", range = 1..100, value = 100)

        taskD.addParameters(greeting, rangeFrom, rangeTo)
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
        //println( "Example Task Sleeping")
        //Thread.sleep(1000)
        //println( "Example Task Ended")
    }
}

fun main(args: Array<String>) {
    CommandTask(Example()).go(args)
}