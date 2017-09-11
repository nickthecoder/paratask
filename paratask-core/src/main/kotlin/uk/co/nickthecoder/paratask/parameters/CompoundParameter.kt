/*
ParaTask Copyright (C) 2017  Nick Robinson>

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

import javafx.beans.property.SimpleStringProperty
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.util.escapeNL
import uk.co.nickthecoder.paratask.util.uncamel
import uk.co.nickthecoder.paratask.util.unescapeNL

/**
 * Very similar to a GroupParameter, but is specially designed to be used as the children of MultipleParameter,
 * which requires its children to be ValueParameters.
 *
 * The 'value' of this parameter is always 'this'.
 */
open class CompoundParameter(
        name: String,
        override val label: String = name.uncamel(),
        description: String = "")

    : ValueParameter<CompoundParameter>, AbstractGroupParameter(
        name = name,
        label = label,
        description = description) {

    override val expressionProperty = SimpleStringProperty()

    override var expression: String?
        get() = expressionProperty.get()
        set(v) {
            expressionProperty.set(v)
        }

    override var value: CompoundParameter
        get() = this
        set(newValue) {
            for (child in children) {
                if (child is ValueParameter<*>) {
                    child.stringValue = (newValue.find(child.name) as ValueParameter<*>).stringValue
                }
            }
        }

    override fun errorMessage(v: CompoundParameter?): String? {
        return null
    }

    override val converter = object : StringConverter<CompoundParameter>() {

        override fun fromString(str: String): CompoundParameter? {
            val lines = str.split("\n")
            for (line in lines) {
                val eq = line.indexOf("=")
                if (eq > 0) {
                    val childName = line.substring(0, eq)
                    val stringValue = line.substring(eq + 1).unescapeNL()
                    val child = find(childName)
                    if (child is ValueParameter<*>) {
                        child.stringValue = stringValue
                    }
                }
            }

            return this@CompoundParameter
        }

        override fun toString(obj: CompoundParameter): String {
            return obj.descendants().filter { it is ValueParameter<*> }.map {
                if (it is ValueParameter<*>) {
                    val strValue = it.expression ?: it.stringValue ?: "?"
                    "${it.name}=${strValue.escapeNL()}"
                } else {
                    "" // Won't ever be used due to the filter above
                }
            }.joinToString(separator = "\n")

        }
    }

    override fun copy(): GroupParameter {
        val copy = GroupParameter(name = name, label = label, description = description)
        copyAbstractAttributes(copy)
        return copy
    }
}
