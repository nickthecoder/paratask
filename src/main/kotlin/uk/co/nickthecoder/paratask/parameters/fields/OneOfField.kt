package uk.co.nickthecoder.paratask.parameters.fields

import javafx.application.Platform
import javafx.scene.Node
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.OneOfParameter
import uk.co.nickthecoder.paratask.parameters.Parameter

class OneOfField(val oneOfParameter: OneOfParameter)
    : ParameterField(oneOfParameter), WrappableField {

    val choiceP = ChoiceParameter("choose", label = oneOfParameter.message, value = oneOfParameter.value)

    val parametersForm = ParametersForm( oneOfParameter )

    init {
        this.control = parametersForm

        for (child in oneOfParameter.children) {
            choiceP.choice(child.name, child, child.label)
        }
        choiceP.valueProperty.bindBidirectional(oneOfParameter.valueProperty)
        choiceP.listen { onChanged() }
        choiceP.parent = oneOfParameter
    }

    fun buildContent() {
        parametersForm.clear()
        parametersForm.buildTop()

        parametersForm.addParameter(choiceP,0)

        oneOfParameter.value?.let { child: Parameter ->
            if (!child.hidden) {
                parametersForm.addParameter(child, 1)
            }
        }
    }

    fun onChanged() {
        Platform.runLater {
            buildContent()
        }
    }

    override fun wrap(): Node {
        return WrappedField(this)
    }
}
