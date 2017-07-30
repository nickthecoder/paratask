package uk.co.nickthecoder.paratask.util

import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.GroupParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.ValueParameter

object JsonHelper {

    fun parametersAsJsonArray(group: GroupParameter): JsonArray {
        val jparameters = JsonArray()
        for (parameter in group.descendants()) {
            if (parameter is ValueParameter<*>) {
                val jparameter = JsonObject()
                jparameter.set("name", parameter.name)
                saveValue(parameter, jparameter)
                jparameters.add(jparameter)
            }
        }
        return jparameters
    }

    private fun saveValue(parameter: ValueParameter<*>, jparameter: JsonObject) {
        if (parameter is MultipleParameter<*>) {
            val jvalues = JsonArray()
            jparameter.set("values", jvalues)
            parameter.innerParameters.forEach { inner ->
                jvalues.add(inner.stringValue)
            }
        } else {
            jparameter.set("value", parameter.stringValue)
        }

    }

    fun parametersAsJsonArray(task: Task): JsonArray {
        return parametersAsJsonArray(task.taskD.root)
    }

    fun read(jparameters: JsonArray, group: GroupParameter) {
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
                                parameter.newValue().stringValue = jvalue.asString()
                            }
                        }
                        continue
                    }

                    val value = ji.getString("value", null)
                    if (value != null) {
                        parameter.stringValue = value
                    }
                }
            }
        }
    }

    fun read(jparameters: JsonArray, task: Task) {
        read(jparameters, task.taskD.root)
    }
}
