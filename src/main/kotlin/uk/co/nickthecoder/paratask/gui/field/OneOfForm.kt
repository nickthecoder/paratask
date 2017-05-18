package uk.co.nickthecoder.paratask.gui.field

import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.scene.control.ComboBox
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.parameter.OneOfParameter
import uk.co.nickthecoder.paratask.parameter.Parameter

class OneOfForm(var oneOfParameter: OneOfParameter)
    : ParametersForm(oneOfParameter) {

    val comboBox = ComboBox<String?>()

    val comboBoxField = ParameterField(parameter)


    val converter: StringConverter<String?> = object : StringConverter<String?>() {
        override fun toString(v: String?): String {
            for (child in oneOfParameter.children) {
                if (child.name == v) {
                    return child.label
                }
            }
            return ""
        }

        override fun fromString(v: String): String? {
            for (child in oneOfParameter.children) {
                if (child.label == v) {
                    return child.name
                }
            }
            return null
        }
    }

    init {
        comboBox.converter = converter
        comboBox.items.clear()
        for (child in oneOfParameter.children) {
            comboBox.items.add(child.name)
        }
        comboBox.valueProperty().addListener(object : ChangeListener<String?> {
            override fun changed(ov: ObservableValue<out String?>?, old: String?, new: String?) {
                onChanged()
            }
        })
        comboBoxField.control = comboBox
        comboBox.valueProperty().bindBidirectional(oneOfParameter.valueProperty)
    }


    override fun buildTop() {
        super.buildTop()

        addField(comboBoxField)
    }

    override fun buildChildren() {
        println("Building oneofchild ${oneOfParameter.value} -> ${oneOfParameter.chosenParameter()}")
        oneOfParameter.chosenParameter()?.let { child: Parameter ->
            if (!child.hidden) {
                addParameter(child, 0)
            }
        }
    }

    fun onChanged() {
        Platform.runLater {
            children.clear()
            fieldSet.clear()
            buildContent()
        }
    }
}
