package uk.co.nickthecoder.paratask.parameter

import uk.co.nickthecoder.paratask.gui.field.ParameterField
import uk.co.nickthecoder.paratask.util.Labelled

interface Parameter : Labelled {

    val name: String

    val description: String

    var parent: GroupParameter?

    fun isStretchy(): Boolean

    fun errorMessage(): String?

    fun createField(): ParameterField

    fun findRoot(): GroupParameter? {
        return parent?.findRoot()
    }

}
