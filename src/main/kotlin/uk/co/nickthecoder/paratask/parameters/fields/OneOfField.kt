package uk.co.nickthecoder.paratask.parameters.fields

import javafx.application.Platform
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.OneOfParameter
import uk.co.nickthecoder.paratask.parameters.Parameter

class OneOfField(val oneOfParameter: OneOfParameter)
    : ParameterField(oneOfParameter), WrappableField {

    val choiceP = ChoiceParameter("choose", label = oneOfParameter.message, value = oneOfParameter.value)

    val parametersForm = ParametersForm(oneOfParameter)

    init {
        control = parametersForm

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

        parametersForm.addParameter(choiceP, 0)

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

    override fun computePrefHeight(width: Double): Double {
        return insets.top + insets.bottom + (control?.prefHeight(width) ?: 0.0)
    }

    override fun computePrefWidth(height: Double): Double {
        return insets.left + insets.right + (control?.prefWidth(height) ?: 0.0)
    }

    override fun layoutChildren() {
        layoutInArea(control, insets.left, insets.top, width - insets.left - insets.right, height - insets.left - insets.right, 0.0, HPos.LEFT, VPos.CENTER)
    }

    override fun wrap(): Node {
        return WrappedField(this)
    }
}
