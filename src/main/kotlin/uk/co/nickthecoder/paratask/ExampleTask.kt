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
import uk.co.nickthecoder.paratask.parameters.*
import java.time.format.DateTimeFormatter

class ExampleTask : AbstractTask() {

    override val taskD = TaskDescription(
            name = "example",
            description = """
This is an example showing various types of parameters. You can look at the source code on git hub :

https://github.com/nickthecoder/paratask/

This class (Example.kt) can be found in package uk.co.nickthecoder.paratask.
"""
    )

    val simpleStringP = StringParameter("simpleString", value = "Hello")
    val yesNoP = BooleanParameter("yesNo", label = "Yes / No")
    val yesNoMaybeP = BooleanParameter("yesNoManybe", label = "Yes / No / Maybe", required = false)
    val fileP = FileParameter("file")
    val directoryP = FileParameter("directory", expectFile = false)

    val dateP = DateParameter("date")

    val isoDateP = DateParameter("isoDate", dateFormat = DateTimeFormatter.ISO_DATE)

    val choiceP = ChoiceParameter("choice", value = Color.BLUE)
            .choice("red", Color.RED)
            .choice("green", Color.GREEN)
            .choice("blue", Color.BLUE)
            .choice("white", Color.WHITE)
            .choice("white", Color.BLACK)

    val groupP = GroupParameter("group", description = "Here we see GroupParameter in action")
    val rangeFromP = IntParameter("rangeFrom", label = "From", range = 1..100, value = 1)
    val rangeToP = IntParameter("rangeTo", label = "To", range = 1..100, value = 99)

    val oneOfP = OneOfParameter("oneOf", description = "We can either enter an Int or a String")
    val aP = StringParameter("a")
    val bP = IntParameter("b")

    val multipleP = MultipleParameter("multiple") { IntParameter("", range = 1..10) }

    val taskP = TaskParameter("task")

    init {
        //taskD.addParameters(dateP, isoDateP)
        taskD.addParameters(
                taskP, simpleStringP, yesNoP, dateP, isoDateP, yesNoMaybeP,
                fileP, directoryP, choiceP, groupP, oneOfP, multipleP)
        
        groupP.addParameters(rangeFromP, rangeToP)
        oneOfP.addParameters(aP, bP)
    }

    override fun run() {
        println("Example Parameter values : ")

        println(taskD.toString())
    }

    override fun customCheck() {
        if (rangeFromP.value!! > rangeToP.value!!) {
            throw ParameterException(rangeToP, "Must be less than 'from'")
        }
    }
}

fun main(args: Array<String>) {
    TaskParser(ExampleTask()).go(args)
}
