package uk.co.nickthecoder.paratask.parameters.fields

import javafx.scene.Node
import uk.co.nickthecoder.paratask.parameters.GroupParameter

/**
 * This is the Field created by GroupParameter and is also use in TaskForm to prompt a whole Task.
 */
open class GroupField(val groupParameter: GroupParameter)
    : ParameterField(groupParameter), WrappableField
{
    val parametersForm = ParametersForm( groupParameter )

    init {
        this.control = parametersForm
    }

    fun buildContent() {
        parametersForm.buildContent()
    }

    override fun wrap(): Node {
        return WrappedField(this)
    }
}
