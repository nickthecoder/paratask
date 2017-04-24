package uk.co.nickthecoder.paratask.parameter

import javafx.scene.Node

interface Parameter {

    val name: String

    val label: String

    fun createField(values: Values): Node

    fun isStretchy(): Boolean

    fun errorMessage(values: Values): String?

    fun createValue(): Value<*>

    fun copyValue(source: Values): Value<*>

}