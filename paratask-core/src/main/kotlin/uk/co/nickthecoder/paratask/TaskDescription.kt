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

package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.util.Labelled
import uk.co.nickthecoder.paratask.util.uncamel
import java.lang.Integer.min

class TaskDescription(
        val name: String = "",
        override val label: String = name.uncamel(),
        val description: String = "",
        val width: Int? = null,
        val height: Int? = null) : Labelled {

    val root: RootParameter = RootParameter(taskD = this, description = description)

    var resolver: ParameterResolver = PlainDirectoryResolver()

    var programmingMode: Boolean = false

    /**
     * When used on a osCommand line, this parameter can be used without using its parameter name.
     * It is good practice to place a "--" argument before the unnamed arguments to avoid
     * unnamed arguments beginning with "--" being interpreted as regular (named) arguments.
     */
    var unnamedParameter: ValueParameter<*>? = null

    fun valueParameters(): List<ValueParameter<*>> = root.valueParameters()

    fun addParameters(vararg parameters: Parameter): TaskDescription {
        parameters.forEach { root.add(it) }
        return this
    }

    fun removeParameters(vararg parameters: Parameter) {
        parameters.forEach { root.remove(it) }
    }

    fun removeParameter(parameter: Parameter) {
        root.remove(parameter)
    }

    fun copyValuesFrom(source: TaskDescription) {
        for (sourceParameter in source.root.descendants()) {
            if (sourceParameter is ValueParameter<*> && sourceParameter.saveValue()) {

                val stringValue = sourceParameter.stringValue
                val destParameter = root.find(sourceParameter.name)
                if (destParameter is ValueParameter<*>) {
                    destParameter.stringValue = stringValue
                    destParameter.expression = sourceParameter.expression
                }

                if (sourceParameter is MultipleParameter<*, *>
                        && destParameter is MultipleParameter<*, *>
                        && destParameter.expression == null) {

                    val size = min(destParameter.innerParameters.size, sourceParameter.innerParameters.size)
                    for (i in 0..size - 1) {
                        destParameter.innerParameters[i].expression = sourceParameter.innerParameters[i].expression
                    }
                }

            }
        }

        // We need special processing for OneOfParameters, because their choices are themselves PARAMETERS, which
        // have been copied. So create duplicate choices
        source.root.descendants().filterIsInstance<OneOfParameter>().forEach { oneOfSource ->
            source.root.find(oneOfSource.name)?.let { oneOfDest ->
                if (oneOfDest is OneOfParameter) {
                    oneOfSource.choices().forEach { (key, _, _) ->
                        oneOfDest.addChoice(key, source.root.find(key))
                    }
                }
            }
        }

    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.appendln("TaskDescription $name")
        builder.appendln()
        for (parameter in valueParameters()) {
            if (parameter is MultipleParameter<*, *> && parameter.expression == null) {
                builder.appendln("    ${parameter.name} = MultipleParameter size ${parameter.innerParameters.size} ${parameter.value.size}")
                for (innerParameter in parameter.innerParameters) {
                    if (innerParameter.expression == null) {
                        builder.appendln("        ${innerParameter.value}")
                    } else {
                        builder.appendln("        Expression(${innerParameter.expression})")
                    }
                }
            } else {
                if (parameter.expression == null) {
                    builder.appendln("    ${parameter.name} = ${parameter.value}")
                } else {
                    builder.appendln("    ${parameter.name} = Expression(${parameter.expression})")
                }
            }
        }
        return builder.toString()
    }
}
