/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask

import javafx.scene.paint.Color
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.GroupParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.OneOfParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.parameters.TaskParameter

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

    val group = GroupParameter("group", description = "Here we see GroupParameter in action")
    val rangeFrom = IntParameter("rangeFrom", label = "From", range = 1..100, value = 1)
    val rangeTo = IntParameter("rangeTo", label = "To", range = 1..100, value = 99)

    val oneOf = OneOfParameter("oneOf", description = "We can either enter an Int or a String")
    val a = StringParameter("a")
    val b = IntParameter("b")

    val multiple = MultipleParameter("multiple") { IntParameter("", range = 1..10) }

    val task = TaskParameter("task")

    init {
        //taskD.addParameters(oneOf)
        //taskD.addParameters(task)
        taskD.addParameters(multiple) // BUG!
        //taskD.addParameters(yesNo)
        //taskD.addParameters(task, simpleString, yesNo, yesNoMaybe, file, directory, choice, group, oneOf, multiple)
        group.addParameters(rangeFrom, rangeTo)
        oneOf.addParameters(a, b)
    }

    override fun run() {
        println("Example Parameter values : ")

        println(taskD.toString())
    }

    override fun customCheck() {
        if (rangeFrom.value!! > rangeTo.value!!) {
            throw ParameterException(rangeTo, "Must be less than 'from'")
        }
    }
}

fun main(args: Array<String>) {
    TaskParser(ExampleTask()).go(args)
}
