package uk.co.nickthecoder.paratask.util

import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.*

object JsonHelper {

    fun parametersAsJsonArray(group: AbstractGroupParameter): JsonArray {
        val jparameters = JsonArray()
        for (parameter in group.descendants()) {
            if (parameter is ValueParameter<*> && !parameter.hidden) {
                val jparameter = JsonObject()
                jparameter.set("name", parameter.name)
                saveValue(parameter, jparameter)
                jparameters.add(jparameter)
            }
        }
        return jparameters
    }

    fun parametersAsJsonArray(task: Task): JsonArray {
        return parametersAsJsonArray(task.taskD.root)
    }

    private fun saveValue(parameter: ValueParameter<*>, jparameter: JsonObject) {
        if (parameter is MultipleParameter<*>) {
            val jvalues = JsonArray()
            jparameter.set("values", jvalues)

            parameter.innerParameters.forEach { inner ->
                if (inner is CompoundParameter) {
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
            } else {
                jparameter.set("expression", parameter.expression)
            }
        }
    }

    fun read(jparameters: JsonArray, group: AbstractGroupParameter, task: Task? = null) {
        for (jitem in jparameters.asArray()) {

            val ji = jitem.asObject()
            val name = ji.getString("name", null)
            if (name != null) {
                val parameter = group.find(name)

                if (parameter is MultipleParameter<*>) {
                    val jvalues = ji.get("values")
                    if (jvalues != null) {
                        parameter.clear()
                        val jvaluesArray = jvalues.asArray()
                        for (jvalue in jvaluesArray) {
                            val newValue = parameter.newValue()
                            if (jvalue.isString) {
                                // Backward compatability.
                                newValue.stringValue = jvalue.asString()
                            } else if (jvalue.isArray && newValue is CompoundParameter) {
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
                        val task2 = Task.create(creationString)
                        val jps = ji.get("parameters")
                        if (jps != null) {
                            read(jps.asArray(), task2)
                        }
                        parameter.value = task2
                        continue
                    }
                } else {
                    val expression = ji.getString("expression", null)
                    val value = ji.getString("value", null)

                    if (parameter is ValueParameter<*>) {

                        if (expression == null) {
                            if (value != null) {
                                parameter.stringValue = value
                            }
                        } else {
                            parameter.expression = expression
                        }

                    } else if (task != null) {
                        task.loadProblem(name, expression, value)
                    }
                }
            }
        }


    }

    fun read(jparameters: JsonArray, task: Task) {
        read(jparameters, task.taskD.root, task)
    }
}
