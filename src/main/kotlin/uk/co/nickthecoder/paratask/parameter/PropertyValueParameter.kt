package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.ObjectProperty

interface PropertyValueParameter<T> : ValueParameter<T> {

    val valueProperty: ObjectProperty<T>

    override var value: T
        get() = valueProperty.get()
        set(v) {
            valueProperty.set(v)
        }
}