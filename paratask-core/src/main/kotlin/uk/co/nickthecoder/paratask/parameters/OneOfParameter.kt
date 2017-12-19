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

import uk.co.nickthecoder.paratask.util.uncamel

class OneOfParameter(
        name: String,
        required: Boolean = true,
        label: String = name.uncamel(),
        val choiceLabel: String,
        description: String = "",
        value: Parameter? = null)

    : ChoiceParameter<Parameter?>(name, label = label, description = description, required = required, value = value) {

    init {
        this.value = value

        listen {
            choices().forEach { choice ->
                choice.value?.hidden = choice.value != value
            }
        }
    }

    override fun addChoice(key: String, value: Parameter?, label: String): OneOfParameter {
        val parameter = value
        super.addChoice(key, parameter, label)
        parameter?.hidden = this.value != parameter
        return this
    }

    fun addChoices(vararg parameters: Parameter): OneOfParameter {
        parameters.forEach { parameter ->
            addChoice(parameter.name, parameter, parameter.label)
        }
        return this
    }

    fun addChoices(parameterMap: Map<String, Parameter>): OneOfParameter {
        parameterMap.forEach { label, parameter ->
            addChoice(parameter.name, parameter, label)
            parameter.hidden = parameter != value
        }
        return this
    }

    fun addChoices(vararg labelledParameters: Pair<String, Parameter>): OneOfParameter {
        labelledParameters.forEach { (label, parameter) ->
            addChoice(parameter.name, parameter, label)
            parameter.hidden = parameter != value
        }
        return this
    }

    override fun copy(): OneOfParameter {
        val result = OneOfParameter(name = name, label = label, choiceLabel = choiceLabel, description = description, value = null)
        return result
    }
}
