package uk.co.nickthecoder.paratask.parameter

import javafx.scene.Node
import uk.co.nickthecoder.paratask.util.Labelled

interface Parameter : Labelled {

    val name: String

    val description: String

    fun isStretchy(): Boolean

    fun errorMessage(values: Values): String?

    fun createField(values: Values): Node

}