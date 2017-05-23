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

import uk.co.nickthecoder.paratask.parameters.fields.ResourceField
import uk.co.nickthecoder.paratask.util.Resource
import uk.co.nickthecoder.paratask.util.uncamel

class ResourceParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: Resource? = null,
        required: Boolean = true)

    : AbstractValueParameter<Resource?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

    override val converter = Resource.converter

    override fun errorMessage(v: Resource?): String? {
        if (isProgrammingMode()) return null

        if (v == null) {
            return super.errorMessage(v)
        }

        if (v.file?.exists() == false) {
            return "Must either be a valid URL, or a file that exists"
        }

        if (v.file?.isDirectory() == true) {
            return "Cannot be a directory"
        }

        return null
    }

    override fun isStretchy(): Boolean = true

    override fun createField() = ResourceField(this)

    override fun toString() = "Resource" + super.toString()

}
