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

package uk.co.nickthecoder.paratask.options

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import groovy.lang.Binding
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.misc.FileListener
import uk.co.nickthecoder.paratask.misc.FileWatcher
import uk.co.nickthecoder.paratask.util.Resource
import java.io.BufferedWriter
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.file.Path

class FileOptions(val resource: Resource) : FileListener {

    val name = resource.nameWithoutExtension

    val includes = mutableListOf<String>()

    var comments: String = ""

    var rowFilterScript: GroovyScript? = null

    private val optionsMap = mutableMapOf<String, Option>()

    private val primaryOptionsMap = mutableMapOf<String, Option>()

    private var saving: Boolean = false

    init {
        load()
        resource.file?.let { FileWatcher.instance.register(it, this) }
    }

    fun listOptions(): Collection<Option> {
        return primaryOptionsMap.values
    }

    fun listIncludes(): Collection<String> {
        return includes
    }

    fun find(code: String): Option? {
        return optionsMap[code]
    }

    fun acceptRow(row: Any?): Boolean {
        if (rowFilterScript == null) {
            return true
        }
        if (row == null) {
            return false
        }
        val binding = Binding()
        binding.setVariable("row", row)
        return rowFilterScript?.run(binding) == true
    }

    fun renameOption(option: Option, newCode: String, newAliases: List<String>) {
        removeOption(option)
        option.code = newCode
        option.aliases = newAliases.toMutableList()
        addOption(option)
    }

    fun addOption(option: Option) {
        primaryOptionsMap.put(option.code, option)
        optionsMap.put(option.code, option)
        option.aliases.forEach { optionsMap.put(it, option) }
    }

    fun removeOption(option: Option) {
        primaryOptionsMap.values.remove(option)

        while (optionsMap.values.remove(option)) {
        }
    }

    fun update(option: Option) {
        removeOption(option)
        addOption(option)
    }

    override fun fileChanged(path: Path) {
        if (!saving) {
            load()
        }
    }

    /*
     Example JSON file :
 
    {
         "comments" : "Applies only to files (not directories)",
         "rowFilterScript" : "row.isFile()",
         "includes" : [ "foo", "bar" ],
         "options" : [
             {
                "type" : "groovy",
                "script" : "println( 1 + 1 ) // Any groovy code!",
                "code" : "2",
                "label" : "Two",
                "isRow" : false,
                "isMultiple" : false,
                "newTab" : false,
                "prompt" : false,
                "refreshResults" : true
             },
             {
                "type" : "task",
                "task" : "uk.co.nickthecoder.paratask.MyTask",
                "code" : "5",
                "label" : "Five",
                "script" : "",
                "isRow" : false,
                "isMultiple" : false,
                "newTab" : false,
                "prompt" : false,
                "refreshResults" : true,
                "parameters" : [
                    "parameter" : { "name" : "foo", value="Hello" },
                    "parameter" : { "name" : "bar", expression="1+1" }
                ]
             }
         ] 

    }
     */

    // https://github.com/ralfstx/minimal-json
    fun load() {
        includes.clear()
        optionsMap.clear()
        primaryOptionsMap.clear()

        val jroot: JsonObject
        try {
            jroot = Json.parse(InputStreamReader(resource.url.openStream())).asObject()
        } catch (e: Exception) {
            // We don't care if we can't read the file - it may not exist, and that's fine!
            //println("Failed to load options file ${resource}")
            return
        }

        comments = jroot.getString("comments", "")
        val rowFilterScriptSource = jroot.getString("rowFilterScript", "")
        rowFilterScript = if (rowFilterScriptSource == "") null else GroovyScript(rowFilterScriptSource)

        val jincludes = jroot.get("includes")
        jincludes?.let {
            jincludes.asArray().mapTo(includes) { it.asString() }
        }

        val joptions = jroot.get("options")
        joptions?.let {
            for (joption1 in joptions.asArray()) {
                val joption = joption1.asObject()
                val type = joption.getString("type", "groovy")

                val option: Option
                when (type) {
                    "groovy" -> {
                        option = GroovyOption(joption.getString("script", ""))
                    }
                    "task" -> {
                        option = TaskOption(joption.getString("task", ""))
                    }
                    else -> {
                        throw RuntimeException("Unknown option type : " + type)
                    }
                }

                with(option) {
                    code = joption.getString("code", "?")
                    label = joption.getString("label", "")
                    isRow = joption.getBoolean("isRow", false)
                    isMultiple = joption.getBoolean("isMultiple", false)
                    newTab = joption.getBoolean("newTab", false)
                    prompt = joption.getBoolean("prompt", false)
                    refresh = joption.getBoolean("refresh", false)
                }

                val keyCodeS = joption.getString("keyCode", "")
                if (keyCodeS == "") {
                    option.shortcut = null
                } else {
                    val keyCode = KeyCode.valueOf(keyCodeS)
                    val shiftS = joption.getString("shift", "UP")
                    val controlS = joption.getString("control", "UP")
                    val altS = joption.getString("alt", "UP")

                    val shift = KeyCombination.ModifierValue.valueOf(shiftS)
                    val control = KeyCombination.ModifierValue.valueOf(controlS)
                    val alt = KeyCombination.ModifierValue.valueOf(altS)

                    option.shortcut = KeyCodeCombination(keyCode, shift, control, alt, KeyCombination.ModifierValue.UP, KeyCombination.ModifierValue.UP)
                }

                val jaliases = joption.get("aliases")
                jaliases?.let {
                    option.aliases = jaliases.asArray().map { it.asString() }.toMutableList()
                }

                // Load parameter values/expressions for TaskOption
                if (option is TaskOption) {
                    val jparameters = joption.get("parameters").asArray()
                    for (jp in jparameters) {
                        val jparameter = jp.asObject()
                        val name = jparameter.getString("name", "")
                        val parameter = option.task.taskD.root.find(name)
                        val jexpression = jparameter.get("expression")
                        if (parameter is MultipleParameter<*> && jexpression == null) {
                            // Special handling, because it contains multiple values
                            val jvalues1 = jparameter.get("values")
                            if (jvalues1 == null) {
                                println("Values not found for option '${option.code}' parameter '${parameter.name}' in $resource. Skipping")
                                continue
                            }
                            val jvalues = jparameter.get("values").asArray()
                            for (jvaluesItem in jvalues) {
                                val innerParameter = parameter.newValue()
                                val jvi = jvaluesItem.asObject()
                                val jvalue = jvi.get("value")
                                if (jvalue == null) {
                                    innerParameter.expression = jvi.getString("expression", "?missing value?")
                                } else {
                                    innerParameter.stringValue = jvalue.asString()
                                }
                            }
                            // Note, the above cannot handle MultipleParameters inside MultipleParameters!!
                            // Should refactor with a "loadParameter" method, and use recursion.
                            continue
                        }
                        if (parameter is ValueParameter<*>) {
                            val jvalue = jparameter.get("value")
                            if (jvalue == null) {
                                parameter.expression = jparameter.getString("expression", "?missing value?")
                            } else {
                                parameter.stringValue = jvalue.asString()
                            }
                        }
                    }
                }

                addOption(option)
            }
        }
    }

    fun save() {
        val file = resource.file ?: return

        val jroot = JsonObject()

        if (comments != "") {
            jroot.add("comments", comments)
        }
        rowFilterScript?.let { jroot.add("rowFilterScript", it.source) }

        val jincludes = JsonArray()
        for (include in includes) {
            jincludes.add(include)
        }
        jroot.add("includes", jincludes)

        val joptions = JsonArray()
        for (option in listOptions()) {
            val joption = JsonObject()

            when (option) {
                is GroovyOption -> {
                    joption.set("type", "groovy")
                    joption.set("script", option.script)
                }
                is TaskOption -> {
                    joption.set("type", "task")
                    joption.set("task", option.task.creationString())
                }
                else -> {
                    throw RuntimeException("Unknown Option : ${option.javaClass}")
                }
            }
            with(joption) {
                set("code", option.code)
                set("label", option.label)
                set("isRow", option.isRow)
                set("isMultiple", option.isMultiple)
                set("prompt", option.prompt)
                set("newTab", option.newTab)
                set("refresh", option.refresh)

                option.shortcut?.let {
                    set("keyCode", it.code.toString())
                    saveModifier(joption, "shift", it.shift)
                    saveModifier(joption, "control", it.control)
                    saveModifier(joption, "alt", it.alt)
                }
            }


            if (option.aliases.size > 0) {
                val jaliases = JsonArray()
                for (alias in option.aliases) {
                    jaliases.add(alias)
                }
                joption.add("aliases", jaliases)
            }
            if (option is TaskOption) {
                val jparameters = JsonArray()
                for (parameter in option.task.valueParameters()) {
                    val jparameter = JsonObject()
                    jparameter.add("name", parameter.name)

                    if (parameter is MultipleParameter<*> && parameter.expression == null) {
                        val jvalues = JsonArray()
                        jparameter.add("values", jvalues)
                        for (innerParameter in parameter.innerParameters) {
                            val jobj = JsonObject()
                            if (innerParameter.expression == null) {
                                jobj.add("value", innerParameter.stringValue)
                            } else {
                                jobj.add("expression", innerParameter.expression)
                            }
                            jvalues.add(jobj)
                        }
                    } else {
                        if (parameter.expression == null) {
                            jparameter.add("value", parameter.stringValue)
                        } else {
                            jparameter.add("expression", parameter.expression ?: "")
                        }
                    }
                    jparameters.add(jparameter)
                }
                joption.add("parameters", jparameters)
            }

            joptions.add(joption)

        }
        jroot.add("options", joptions)

        saving = true
        try {
            BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use {
                jroot.writeTo(it, PrettyPrint.indentWithSpaces(4))
            }
        } finally {
            saving = false
        }
    }

    private fun saveModifier(joption: JsonObject, name: String, mod: KeyCombination.ModifierValue) {
        if (mod != KeyCombination.ModifierValue.UP) {
            joption.set(name, mod.toString())
        }
    }
}
