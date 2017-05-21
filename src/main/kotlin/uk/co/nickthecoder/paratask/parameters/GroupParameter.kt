package uk.co.nickthecoder.paratask.parameters

import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.parameters.fields.GroupField
import uk.co.nickthecoder.paratask.parameters.fields.ParameterField
import uk.co.nickthecoder.paratask.parameters.fields.ParametersForm
import uk.co.nickthecoder.paratask.util.uncamel

open class GroupParameter(
        name: String,
        override val label: String = name.uncamel(),
        description: String = "")

    : AbstractGroupParameter(
        name = name,
        label = label,
        description = description)
{
    /**
     * Creates a GroupField, which contains fields for each of this group's children
     * When used to group parameters it is wrapped in a box, but when used as the root,
     * it is not wrapped in a box.
     */
    override fun createField(): GroupField {
        val result = GroupField(this)
        result.buildContent()
        return result
    }
    
    override fun errorMessage(): String? = null

}
