package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.ObjectProperty

interface PropertyValueParameter<T> : ValueParameter<T> {

    val property: ObjectProperty<T>

    override var value: T
        get() = property.get()
        set(v) {
            property.set(v)
        }
}