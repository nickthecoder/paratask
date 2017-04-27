package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleObjectProperty

class StringValue(
        override val parameter: StringParameter,
        initialValue: String = "")

    : AbstractValue<String>(initialValue) {

    override fun toString(v: String) = v

    override fun fromString(v: String) = v

    override fun errorMessage(v: String) = parameter.errorMessage(v)

    override fun toString(): String = "StringValue name '${parameter.name}' = '${value}'"

}