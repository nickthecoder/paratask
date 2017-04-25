package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.BooleanParameter
import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.IntParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.ValueParameter
import uk.co.nickthecoder.paratask.parameter.Values

class Example : SimpleTask() {

    override val taskD = TaskDescription(
            name = "example",
            description = """
This is an example showing various types of parameters. You can look at the source code on git hub :

https://github.com/nickthecoder/paratask/
 
This class (Example.kt) can be found in package uk.co.nickthecoder.paratask.
"""
    )

    val greeting = StringParameter("greeting", value = "Hello")
    val freeBeer = BooleanParameter("freeBeer", label = "Do you want Free Beer?")
    val threeWay = BooleanParameter("threeWay", label = "Yes / No / Maybe", required = false)

    val range = GroupParameter("range", description = """
Here we see GroupParameter in action
""")
    val rangeFrom = IntParameter("rangeFrom", label = "From", range = 1..100, value = 1)
    val rangeTo = IntParameter("rangeTo", label = "To", range = 1..100, value = 99)

    init {
        taskD.addParameters(greeting, freeBeer, threeWay, range)
        range.addParameters(rangeFrom, rangeTo)
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

    override fun check(values: Values) {

        super.check(values)

        val from = rangeFrom.valueFrom(values).value!!
        val to = rangeTo.valueFrom(values).value!!
        if (from > to) {
            throw ParameterException(rangeTo, "Must be less than 'from'")
        }
    }
}

fun main(args: Array<String>) {
    CommandTask(Example()).go(args)
}