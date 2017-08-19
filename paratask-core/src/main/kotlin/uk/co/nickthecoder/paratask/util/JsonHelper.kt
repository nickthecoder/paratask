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
                    if (inner.expression == null) {
                        jvalues.add(inner.stringValue)
                    } else {
                        val jexpression = JsonObject()
                        jexpression.add("expression", inner.expression!!)
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

    fun read(jparameters: JsonArray, group: AbstractGroupParameter) {
        for (jitem in jparameters.asArray()) {

            val ji = jitem.asObject()
            val name = ji.getString("name", null)
            if (name != null) {
                val parameter = group.find(name)
                if (parameter is ValueParameter<*>) {

                    if (parameter is MultipleParameter<*>) {
                        val jvalues = ji.get("values")
                        if (jvalues != null) {
                            val jvaluesArray = jvalues.asArray()
                            for (jvalue in jvaluesArray) {
                                val newValue = parameter.newValue()
                                if (jvalue.isString) {
                                    newValue.stringValue = jvalue.asString()
                                } else if (jvalue.isArray && newValue is CompoundParameter) {
                                    val jvalueArray = jvalue.asArray()
                                    read(jvalueArray, newValue)
                                } else if (jvalue.isObject) {
                                    val value = jvalue.asObject().getString("value", null)
                                    if ( value != null ) {
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
                            val task = Task.create(creationString)
                            val jps = ji.get("parameters")
                            if (jps != null) {
                                read(jps.asArray(), task)
                            }
                            parameter.value = task
                            continue
                        }
                    }

                    val expression = ji.getString("expression", null)
                    if (expression == null) {
                        val value = ji.getString("value", null)
                        if (value != null) {
                            parameter.stringValue = value
                        }
                    } else {
                        parameter.expression = expression
                    }
                }
            }

        }
    }

    fun read(jparameters: JsonArray, task: Task) {
        read(jparameters, task.taskD.root)
    }
}
