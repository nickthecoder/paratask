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

package uk.co.nickthecoder.paratask.parameters

import uk.co.nickthecoder.paratask.parameters.fields.GroupField
import uk.co.nickthecoder.paratask.util.uncamel

open class GroupParameter(
        name: String,
        override val label: String = name.uncamel(),
        description: String = "")

    : AbstractGroupParameter(
        name = name,
        label = label,
        description = description) {

    override fun errorMessage(): String? = null

    override fun isStretchy() = true

    override fun copy(): GroupParameter {
        val result = GroupParameter(name = name, label = label, description = description)
        if (horizontal) {
            result.layoutHorizontal(labelsAbove)
        }
        copyChildren(result)
        return result
    }
}
