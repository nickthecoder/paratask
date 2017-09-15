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
import uk.co.nickthecoder.paratask.parameters.compound.ResourceParameter
import uk.co.nickthecoder.paratask.util.Resource
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.time.format.DateTimeFormatter

class ExampleTask : AbstractTask() {

    override val taskD = TaskDescription(
            name = "example",
            width = 700,
            height = 700,
            description = """
This is an example showing various types of parameters. You can look at the source code on git hub :

https://github.com/nickthecoder/paratask/

This class (Example.kt) can be found in sub-project paratask-app, package uk.co.nickthecoder.paratask.
"""
    )

    val optionalInt = IntParameter("optionalInt", value = null, required = false)

    val requiredInt = IntParameter("requiredInt", value = null)

    val doubleP = DoubleParameter("double")
    val optionalDoubleP = DoubleParameter("optionalDouble", required = false)

    val simpleStringP = StringParameter("simpleString", value = "Hello")
    val yesNoP = BooleanParameter("yesNo", label = "Yes / No")
    val yesNoMaybeP = BooleanParameter("yesNoManybe", label = "Yes / No / Maybe", required = false)
    val fileP = FileParameter("file", extensions = listOf("txt"))
    val directoryP = FileParameter("directory", expectFile = false)

    val resourceP = ResourceParameter("resource", value = Resource(homeDirectory))

    val dateP = DateParameter("date")

    val isoDateP = DateParameter("isoDate", dateFormat = DateTimeFormatter.ISO_DATE)

    val choiceP = ChoiceParameter("choice", value = Color.BLUE)
            .choice("red", Color.RED)
            .choice("green", Color.GREEN)
            .choice("blue", Color.BLUE)
            .choice("white", Color.WHITE)
            .choice("white", Color.BLACK)

    val groupP = GroupParameter("group", description = "Here we see GroupParameter in action")
    val stringInGroupP = StringParameter("units")

    val oneOfP = OneOfParameter("oneOf", description = "We can either enter an Int or a String", choiceLabel = "Choose")
    val aP = StringParameter("a")
    val bP = IntParameter("b")

    val multipleP = MultipleParameter("multiple") { StringParameter("", required = true) }

    val buttonP = ButtonParameter("button", buttonText = "Click Me", action = { println("Clicked!") })

    val taskP = TaskParameter("task", taskFactory = RegisteredTaskFactory())

    val informationP = InformationParameter("information", information = "Use information parameters to add arbitrary text to a form.")

    init {

        taskD.addParameters(requiredInt, optionalInt, doubleP, optionalDoubleP,
                fileP, informationP, buttonP,
                taskP, simpleStringP, yesNoP, dateP, isoDateP, yesNoMaybeP,
                directoryP, resourceP, choiceP, groupP, oneOfP, multipleP)

        oneOfP.addParameters(aP, bP)
    }

    override fun run() {
        println("Example Parameter values : ")

        println(taskD.toString())

    }
}

fun main(args: Array<String>) {
    TaskParser(ExampleTask()).go(args)
}
