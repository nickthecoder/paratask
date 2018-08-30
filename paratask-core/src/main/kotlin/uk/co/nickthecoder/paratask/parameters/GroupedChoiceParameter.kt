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

import uk.co.nickthecoder.paratask.parameters.fields.GroupedChoiceField
import uk.co.nickthecoder.paratask.util.uncamel

class GroupedChoiceParameter<T>(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        hint: String = "",
        value: T? = null,
        val allowSingleItemSubMenus: Boolean = false,
        required: Boolean = true)

    : ChoiceParameter<T>(name, label, description, hint = hint, value = value, required = required) {

    private val groups = mutableMapOf<String, Group>()

    fun groups(): Map<String, Group> = groups

    override fun clear() {
        groups.clear()
        super.clear()
    }

    override fun addChoice(key: String, value: T, label: String): GroupedChoiceParameter<T> {
        group("").choice(key, value, label)
        return this
    }

    fun group(label: String): Group {
        var group = groups[label]
        if (group == null) {
            group = Group(label)
            groups.put(label, group)
        }
        return group
    }

    override fun createField(): GroupedChoiceField<T> {
        val result = GroupedChoiceField<T>(this)
        result.build()
        return result
    }

    override fun copy(): GroupedChoiceParameter<T> {
        val copy = GroupedChoiceParameter<T>(name, label, description, hint = hint,
                value = value, required = required)

        groups.values.forEach { group ->
            val groupCopy = copy.group(group.label)
            group.groupChoices.forEach { choice ->
                groupCopy.choice(choice.key, choice.value, choice.label)
            }
        }
        return copy
    }

    override fun toString(): String = "Grouped" + super.toString()


    inner class Group(val label: String) {
        val groupChoices = mutableListOf<Choice<T>>()

        fun choice(key: String, value: T, label: String = key.uncamel()): Group {
            val choice = Choice(key, value, label)
            groupChoices.add(choice)
            choices.add(choice)
            return this
        }
    }

}
