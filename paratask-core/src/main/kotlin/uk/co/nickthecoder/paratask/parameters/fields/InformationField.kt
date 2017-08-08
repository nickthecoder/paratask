package uk.co.nickthecoder.paratask.parameters.fields

import javafx.scene.control.Label
import uk.co.nickthecoder.paratask.parameters.InformationParameter

class InformationField(val informationParameter: InformationParameter) : ParameterField(informationParameter) {

    val label = Label(informationParameter.information)

    override fun createControl(): Label {

        label.isWrapText = true
        label.styleClass.add("information")
        control = label

        informationParameter.styleProperty.addListener({ _, oldValue, newValue ->
            oldValue?.let { label.styleClass.remove(it) }
            newValue?.let { label.styleClass.add(it) }
        })
        return label
    }

}
