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
package uk.co.nickthecoder.paratask.util

import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskFactory
import uk.co.nickthecoder.paratask.parameters.*

object JsonHelper {

    fun taskAsJsonObject(task: Task): JsonObject {
        val jtask = JsonObject()
        jtask.add("task", task.creationString())
        jtask.add("parameters", parametersAsJsonArray(task))

        return jtask
    }

    fun parametersAsJsonArray(group2: GroupParameter): JsonArray {
        val jparameters = JsonArray()

        fun foo(grp: GroupParameter) {

            for (parameter in grp.children) {
                // Save the VALUE
                if (parameter is ValueParameter<*> && parameter.saveValue() && !parameter.hidden) {
                    val jparameter = JsonObject()
                    jparameter.set("name", parameter.name)
                    saveValue(parameter, jparameter)
                    jparameters.add(jparameter)
                }
                // Save the children's values
                if (parameter is GroupParameter && parameter.saveChildren()) {
                    foo(parameter)
                }
            }
        }
        foo(group2)
        return jparameters
    }

    fun parametersAsJsonArray(task: Task): JsonArray {
        return parametersAsJsonArray(task.taskD.root)
    }

    private fun saveValue(parameter: ValueParameter<*>, jparameter: JsonObject) {
        if (parameter.expression != null) {
            jparameter.set("expression", parameter.expression)
        } else {
            if (parameter is MultipleParameter<*, *>) {
                val jvalues = JsonArray()
                jparameter.set("values", jvalues)

                parameter.innerParameters.forEach { inner ->
                    if (inner is MultipleGroupParameter) {
                        val jArray = parametersAsJsonArray(inner)
                        jvalues.add(jArray)
                    } else {
                        val jvalue = JsonObject()
                        jvalues.add(jvalue)
                        if (inner.expression == null) {
                            jvalue.add("value", inner.stringValue)
                        } else {
                            jvalue.add("expression", inner.expression!!)
                        }
                    }
                }
            } else if (parameter is TaskParameter) {
                val task = parameter.value
                if (task != null) {
                    jparameter.set("task", task.creationString())
                    val jps = parametersAsJsonArray(task)
                    jparameter.set("parameters", jps)
                } else {
                    println("Ignoring null task from TaskParameter $parameter.name")
                }
            } else {
                if (parameter.expression == null) {
                    jparameter.set("value", parameter.stringValue)
                }
            }
        }
    }


    fun readTask(jtask: JsonObject): Task {
        val creationString = jtask.get("task").asString()
        val task = TaskFactory.createTask(creationString)

        val jparameters = jtask.get("parameters")
        if (jparameters != null) {
            JsonHelper.read(jparameters.asArray(), task)
        }
        return task
    }


    fun read(jparameters: JsonArray, group: GroupParameter, task: Task? = null) {
        for (jitem in jparameters.asArray()) {

            val ji = jitem.asObject()
            val name = ji.getString("name", null)
            if (name != null) {
                val parameter = group.find(name)

                val expression = ji.getString("expression", null)

                if (parameter is ValueParameter<*> && expression != null) {
                    parameter.expression = expression

                } else {

                    if (parameter is MultipleParameter<*, *>) {
                        val jvalues = ji.get("values")
                        if (jvalues != null) {
                            parameter.clear()
                            val jvaluesArray = jvalues.asArray()
                            for (jvalue in jvaluesArray) {
                                val newValue = parameter.newValue()
                                if (jvalue.isString) {
                                    // Backward compatability.
                                    newValue.stringValue = jvalue.asString()
                                } else if (jvalue.isArray && newValue is MultipleGroupParameter) {
                                    val jvalueArray = jvalue.asArray()
                                    read(jvalueArray, newValue, task)
                                } else if (jvalue.isObject) {
                                    val value = jvalue.asObject().getString("value", null)
                                    if (value != null) {
                                        newValue.stringValue = value
                                    } else {
                                        val expression = jvalue.asObject().getString("expression", null)
                                        newValue.expression = expression
                                    }
                                }
                            }
                            continue
                        }

                    } else if (parameter is TaskParameter) {
                        val creationString = ji.getString("task", null)
                        if (creationString != null) {
                            val task2 = TaskFactory.createTask(creationString)
                            val jps = ji.get("parameters")
                            if (jps != null) {
                                read(jps.asArray(), task2)
                            }
                            parameter.value = task2
                            continue
                        }
                    } else {
                        val value = ji.getString("value", null)

                        if (parameter is ValueParameter<*> && value != null) {
                            parameter.stringValue = value

                        } else {
                            task?.loadProblem(name, expression, value)
                        }
                    }
                }
            }
        }

    }

    fun read(jparameters: JsonArray, task: Task) {
        read(jparameters, task.taskD.root, task)
    }
}
