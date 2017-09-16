package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.*

class GroupExample : AbstractTask() {

    override val taskD = TaskDescription("groupExample")

    val normalIntP = IntParameter("normalInt", label = "Int")
    val normalStringP = StringParameter("normalString", label = "String", columns = 10)
    val normalGroupP = GroupParameter("normalGroup")
            .addParameters(normalIntP, normalStringP)

    val horizontalIntP = IntParameter("horizontalInt", label = "Int")
    val horizontalStringP = StringParameter("horizontalString", label = "String", columns = 10)
    val horizontalGroupP = GroupParameter("horizontalGroup")
            .addParameters(horizontalIntP, horizontalStringP)
            .asHorizontal()

    val boolAP = BooleanParameter("a")
    val boolBP = BooleanParameter("b")
    val noStretchP = GroupParameter("No stretchy fields")
            .addParameters(boolAP, boolBP)
            .asHorizontal()

    val boolCP = BooleanParameter("c")
    val midStringP = StringParameter("midStr", label = "String", columns = 10)
    val boolDP = BooleanParameter("d")
    val middleStretchyP = GroupParameter("middleStretchy")
            .addParameters(boolCP, midStringP, boolDP)
            .asHorizontal()

    val infoP = InformationParameter("info", information = "Note, that only the first stretchy field in a horizontal group stretches. Resize the window to see.")

    val stringAP = StringParameter("stringA", columns = 10)
    val stringBP = StringParameter("stringB", columns = 10)
    val twoStringsP = GroupParameter("twoStrings")
            .addParameters(stringAP, stringBP)
            .asHorizontal()

    val boolAlphaP = BooleanParameter("alpha")
    val boolBetaP = BooleanParameter("beta")
    val boolGammaP = BooleanParameter("gamma")
    val boolDeltaP = BooleanParameter("delta")
    val gridGroupP = GroupParameter("grid")
            .addParameters(boolAlphaP, boolBetaP, boolGammaP, boolDeltaP)
            .asGrid(columns = 3, isBoxed = true)

    val boolAlpha2P = BooleanParameter("alpha2")
    val boolBeta2P = BooleanParameter("beta2")
    val boolGamma2P = BooleanParameter("gamma2")
    val boolDelta2P = BooleanParameter("delta2")
    val gridGroup2P = GroupParameter("grid2")
            .addParameters(boolAlpha2P, boolBeta2P, boolGamma2P, boolDelta2P)
            .asGrid(labelsAbove = true, columns = 3, isBoxed = true)

    init {
        taskD.addParameters(normalGroupP, horizontalGroupP, noStretchP, middleStretchyP, infoP, twoStringsP, gridGroupP, gridGroup2P)
    }

    override fun run() {
    }

}

fun main(args: Array<String>) {
    TaskParser(GroupExample()).go(args, prompt = true)
}
