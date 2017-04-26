package uk.co.nickthecoder.paratask.tasks

import javafx.scene.paint.Color
import uk.co.nickthecoder.paratask.CommandTask
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameter.BooleanParameter
import uk.co.nickthecoder.paratask.parameter.ChoiceParameter
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.IntParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
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
    val directory = FileParameter("directory")

    val range = GroupParameter("range", description = """
Here we see GroupParameter in action
""")
    val rangeFrom = IntParameter("rangeFrom", label = "From", range = 1..100, value = 1)
    val rangeTo = IntParameter("rangeTo", label = "To", range = 1..100, value = 99)

    val color = ChoiceParameter("color", value = Color.BLUE)
        .choice("red", Color.RED)
        .choice("green", Color.GREEN)
        .choice("blue", Color.BLUE)
        .choice("white", Color.WHITE)
        .choice("white", Color.BLACK)

    init {
        taskD.addParameters(greeting, color, freeBeer, threeWay, range, directory)
        range.addParameters(rangeFrom, rangeTo)
    }


    override fun run(values: Values) {
        println("Example Parameter values : ")

        dumpValues(values)
        //println( "Example Task Sleeping")
        //Thread.sleep(1000)
        //println( "Example Task Ended")
    }

    override fun check(values: Values) {
        val from = rangeFrom.value(values)!!
        val to = rangeTo.value(values)!!
        if (from > to) {
            throw ParameterException(rangeTo, "Must be less than 'from'")
        }
    }
}

fun main(args: Array<String>) {
    CommandTask(Example()).go(args)
}
