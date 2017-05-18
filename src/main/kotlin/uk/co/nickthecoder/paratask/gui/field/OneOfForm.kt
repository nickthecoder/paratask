package uk.co.nickthecoder.paratask.gui.field

import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.ComboBox
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameter.ChoiceParameter
import uk.co.nickthecoder.paratask.parameter.OneOfParameter
import uk.co.nickthecoder.paratask.parameter.Parameter

class OneOfForm(var oneOfParameter: OneOfParameter)
    : ParametersForm(oneOfParameter) {

    val choiceP = ChoiceParameter<String?>("choose", label = oneOfParameter.message, value = oneOfParameter.value)

    init {
        for (child in oneOfParameter.children) {
            choiceP.choice(child.name, child.name, child.label)
        }
        choiceP.property.bindBidirectional(oneOfParameter.property)
        choiceP.listen { onChanged() }
    }

    override fun buildTop() {
        super.buildTop()

        addParameter(choiceP, 0)
    }

    override fun buildChildren() {
        println("Building oneofchild ${oneOfParameter.value} -> ${oneOfParameter.chosenParameter()}")
        oneOfParameter.chosenParameter()?.let { child: Parameter ->
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
