package uk.co.nickthecoder.paratask.parameters.fields

import javafx.application.Platform
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.OneOfParameter
import uk.co.nickthecoder.paratask.parameters.Parameter

class OneOfForm(var oneOfParameter: OneOfParameter)
    : ParametersForm(oneOfParameter) {

    val choiceP = ChoiceParameter("choose", label = oneOfParameter.message, value = oneOfParameter.value)

    init {
        for (child in oneOfParameter.children) {
            choiceP.choice(child.name, child, child.label)
        }
        choiceP.valueProperty.bindBidirectional(oneOfParameter.valueProperty)
        choiceP.listen { onChanged() }
        choiceP.parent = oneOfParameter
    }

    override fun buildTop() {
        super.buildTop()

        addParameter(choiceP, 0)
    }

    override fun buildChildren() {
        oneOfParameter.value?.let { child: Parameter ->
            if (!child.hidden) {
                addParameter(child, 1)
            }
        }
    }

    fun onChanged() {
        println( "onChanged choiceP ${choiceP.value} one ${oneOfParameter.value}")
        Platform.runLater {
            println( "Later onChanged choiceP ${choiceP.value} one ${oneOfParameter.value}")
            children.clear()
            fieldSet.clear()
            buildContent()
        }
    }
}
