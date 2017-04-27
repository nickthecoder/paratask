package uk.co.nickthecoder.paratask.parameter

import javafx.scene.Node

interface Parameter {

    val name: String

    val label: String

    val description: String

    fun createField(values: Values): Node

    fun isStretchy(): Boolean

    fun errorMessage(values: Values): String?

}