package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.*

class HorizontalGroupExample : AbstractTask() {


    val horizontalIntP = IntParameter("horizontalInt", label = "Int")
    val horizontalStringP = StringParameter("horizontalString", label = "String", columns = 10)
    val horizontalGroupP = SimpleGroupParameter("horizontalGroup")
            .addParameters(horizontalIntP, horizontalStringP)
            .asHorizontal()

    val boolAP = BooleanParameter("a")
    val boolBP = BooleanParameter("b")
    val noStretchP = SimpleGroupParameter("No stretchy fields")
            .addParameters(boolAP, boolBP)
            .asHorizontal()

    val boolCP = BooleanParameter("c")
    val midStringP = StringParameter("midStr", label = "String", columns = 10)
    val boolDP = BooleanParameter("d")
    val middleStretchyP = SimpleGroupParameter("middleStretchy")
            .addParameters(boolCP, midStringP, boolDP)
            .asHorizontal()

    val infoP = InformationParameter("info", information = "\nNote, that only the first stretchy field in a horizontal group stretches. Resize the window to see.")

    val stringAP = StringParameter("stringA", columns = 10)
    val stringBP = StringParameter("stringB", columns = 10)
    val twoStringsP = SimpleGroupParameter("twoStrings")
            .addParameters(stringAP, stringBP)
            .asHorizontal()

    val info2P = InformationParameter("info2", information = "\nHere we see a group without labels, and the first StringParameter is made non-stretchy.")

    val houseNumberP = StringParameter("houseNumber", columns = 6, stretchy = false)
    val roadNameP = StringParameter("roadName")
    val addressLine1P = SimpleGroupParameter("addressLine1")
            .addParameters(houseNumberP, roadNameP)
            .asHorizontal(LabelPosition.NONE)

    override val taskD = TaskDescription("horizontalGroupExample",
            description = "Demonstrates the use of SimpleGroupParameter.asHorizontal(…)")
            .addParameters(horizontalGroupP, noStretchP, middleStretchyP, infoP, twoStringsP, info2P, addressLine1P)


    override fun run() {
    }

}

fun main(args: Array<String>) {
    TaskParser(HorizontalGroupExample()).go(args, prompt = true)
}
