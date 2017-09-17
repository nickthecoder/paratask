package uk.co.nickthecoder.paratask.parameters

import javafx.beans.property.SimpleStringProperty

abstract class CompoundParameter<T>(name: String, label: String, description: String = "")

    : GroupParameter(name = name, label = label, description = description),
        ValueParameter<T> {

    override fun saveChildren(): Boolean = false

    override val expressionProperty = SimpleStringProperty()

    override fun errorMessage(v: T?): String? = null
}
