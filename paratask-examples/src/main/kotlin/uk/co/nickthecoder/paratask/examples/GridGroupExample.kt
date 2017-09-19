package uk.co.nickthecoder.paratask.examples

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.parameters.*

class GridGroupExample : AbstractTask() {

    override val taskD = TaskDescription("gridGroupExample",
            description = """Arrange a group of parameters in a grid with three columns.""")

    val boolAlphaP = BooleanParameter("alpha")
    val boolBetaP = BooleanParameter("beta")
    val boolGammaP = BooleanParameter("gamma")
    val boolDeltaP = BooleanParameter("delta")
    val gridGroupP = SimpleGroupParameter("grid", label="Labels on the Left")
            .addParameters(boolAlphaP, boolBetaP, boolGammaP, boolDeltaP)
            .asGrid(columns = 3, isBoxed = true)

    val boolAlpha2P = BooleanParameter("alpha2")
    val boolBeta2P = BooleanParameter("beta2")
    val boolGamma2P = BooleanParameter("gamma2")
    val boolDelta2P = BooleanParameter("delta2")
    val gridGroup2P = SimpleGroupParameter("grid2", label="Labels Above")
            .addParameters(boolAlpha2P, boolBeta2P, boolGamma2P, boolDelta2P)
            .asGrid(LabelPosition.TOP, columns = 3, isBoxed = true)

    val boolAlpha3P = BooleanParameter("alpha3")
    val boolBeta3P = BooleanParameter("beta3")
    val boolGamma3P = BooleanParameter("gamma3")
    val boolDelta3P = BooleanParameter("delta3")
    val gridGroup3P = SimpleGroupParameter("grid3", label="No Labels")
            .addParameters(boolAlpha3P, boolBeta3P, boolGamma3P, boolDelta3P)
            .asGrid(LabelPosition.NONE, columns = 3, isBoxed = true)

    init {
        taskD.addParameters(gridGroupP, gridGroup2P, gridGroup3P)
    }

    override fun run() {
    }

}

fun main(args: Array<String>) {
    TaskParser(GridGroupExample()).go(args, prompt = true)
}
