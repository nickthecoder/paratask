/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask.parameters.fields

import uk.co.nickthecoder.paratask.parameters.GroupParameter

/**
 * This is the [ParameterField] created by [GroupParameter].
 * Note, there are other implementations for AbstractGroupParameter's ParameterField. This implementation lays out its
 * children in a form, with labels on the left, and controls on the right. The controls are aligned with each other.
 */
open class GroupField(groupParameter: GroupParameter, isBoxed: Boolean = true)
    : ParameterField(groupParameter, isBoxed = isBoxed), FieldParent {

    val parametersForm = ParametersForm(groupParameter, this)

    override fun updateError(field: ParameterField) {
        parametersForm.updateField(field)
    }

    override fun updateField(field: ParameterField) {
        parametersForm.updateField(field)
    }

    override fun iterator(): Iterator<ParameterField> = parametersForm.iterator()

    override fun createControl(): ParametersForm {
        buildContent()
        return parametersForm
    }

    fun buildContent() {
        parametersForm.buildContent()
    }

}
