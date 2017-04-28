package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import uk.co.nickthecoder.paratask.parameter.GroupParameter
import uk.co.nickthecoder.paratask.parameter.Parameter
import uk.co.nickthecoder.paratask.parameter.Values

class GroupParametersForm(var groupParameter: GroupParameter, values: Values)
    : ParametersForm(groupParameter) {

    init {
        if (groupParameter.description.length > 0) {
            children.add(TextFlow(Text(groupParameter.description)))
        }
        groupParameter.forEach() { parameter ->
            addParameter(parameter, values)
        }
    }

    fun addParameter(parameter: Parameter, values: Values): Node {

        val node = parameter.createField(values)

        children.add(node)

        val parameterField = if (node is ParameterField) {
            node
        } else if (node is WrappedParameterField) {
            node.parameterField
        } else {
            null
        }

        if (parameterField != null) {
            parameterField.getStyleClass().add("field-${parameter.name}")
            parameterField.form = this
            fieldSet.add(parameterField)
        }

        return node
    }
}