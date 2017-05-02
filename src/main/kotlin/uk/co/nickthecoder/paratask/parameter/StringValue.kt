package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleObjectProperty

class StringValue(
        override val parameter: StringParameter,
        initialValue: String = "")

    : AbstractValue<String>(initialValue) {

    override fun toString(v: String) = v

    override fun errorMessage(v: String): String? {
        return parameter.errorMessage(v
        )
    }

    override fun copy() = StringValue(parameter, value)

    override fun fromString(v: String) = v

}