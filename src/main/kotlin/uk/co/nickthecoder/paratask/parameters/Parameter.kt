package uk.co.nickthecoder.paratask.parameters

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.fields.ParameterField
import uk.co.nickthecoder.paratask.util.Labelled

interface Parameter : Labelled {

    val name: String

    val description: String

    var parent: Parameter?

    val parameterListeners: ParameterListeners

    var hidden: Boolean

    fun listen(listener: (event: ParameterEvent) -> Unit)

    fun isStretchy(): Boolean

    fun errorMessage(): String?

    fun createField(): ParameterField

    fun findRoot(): RootParameter? {
        return parent?.findRoot()
    }

    fun findTaskD(): TaskDescription = parent!!.findTaskD()

    fun isProgrammingMode(): Boolean = findTaskD().programmingMode

}
