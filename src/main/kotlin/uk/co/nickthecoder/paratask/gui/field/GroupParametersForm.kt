package uk.co.nickthecoder.paratask.gui.field

import javafx.scene.Node
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.Parameter
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.parameter.WrappableField

class GroupParametersForm(var groupParameter: GroupParameter, values: Values)
    : ParametersForm(groupParameter) {

    init {
        if (groupParameter.description.length > 0) {
            children.add(TextFlow(Text(groupParameter.description)))
        }
        groupParameter.children().forEach() { parameter ->
            addParameter(parameter, values)
        }
    }

    fun addParameter(parameter: Parameter, values: Values): Node {

        val parameterField = parameter.createField(values)

        val node = if (parameter is WrappableField) {
            parameter.wrap(parameterField)
        } else {
            parameterField
        }

        children.add(node)

        parameterField.getStyleClass().add("field-${parameter.name}")
        parameterField.form = this
        fieldSet.add(parameterField)

        return node
    }
}