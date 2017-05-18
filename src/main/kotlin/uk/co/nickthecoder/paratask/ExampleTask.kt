package uk.co.nickthecoder.paratask

import javafx.scene.paint.Color
import uk.co.nickthecoder.paratask.parameter.BooleanParameter
import uk.co.nickthecoder.paratask.parameter.ChoiceParameter
import uk.co.nickthecoder.paratask.parameter.FileParameter
import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.IntParameter
import uk.co.nickthecoder.paratask.parameter.MultipleParameter
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.TaskParameter

class ExampleTask : AbstractTask() {

    override val taskD = TaskDescription(
            name = "example",
            description = """
This is an example showing various types of parameters. You can look at the source code on git hub :

https://github.com/nickthecoder/paratask/

This class (Example.kt) can be found in package uk.co.nickthecoder.paratask.
"""
    )

    val simpleString = StringParameter("simpleString", value = "Hello")
    val yesNo = BooleanParameter("yesNo", label = "Yes / No")
    val yesNoMaybe = BooleanParameter("yesNoManybe", label = "Yes / No / Maybe", required = false)
    val file = FileParameter("file")
    val directory = FileParameter("directory", expectFile = false)

    val choice = ChoiceParameter("choice", value = Color.BLUE)
            .choice("red", Color.RED)
            .choice("green", Color.GREEN)
            .choice("blue", Color.BLUE)
            .choice("white", Color.WHITE)
            .choice("white", Color.BLACK)

    val group = GroupParameter("group", description = """
Here we see GroupParameter in action
""")
    val rangeFrom = IntParameter("rangeFrom", label = "From", range = 1..100, value = 1)
    val rangeTo = IntParameter("rangeTo", label = "To", range = 1..100, value = 99)


    val multiple = MultipleParameter("multiple") { IntParameter("", range = 1..10) }

    val task = TaskParameter("task")

    init {
        //taskD.addParameters(task, yesNo)
        //taskD.addParameters(multiple) // BUG!
        taskD.addParameters(task, simpleString, yesNo, yesNoMaybe, file, directory, choice, group, multiple)
        group.addParameters(rangeFrom, rangeTo)
    }

    override fun run() {
        println("Example Parameter values : ")

        println(taskD.toString())
    }

    override fun check() {
        if (rangeFrom.requiredValue() > rangeTo.requiredValue()) {
            throw ParameterException(rangeTo, "Must be less than 'from'")
        }
    }
}

fun main(args: Array<String>) {
    CommandLineTask(ExampleTask()).go(args)
}
