package uk.co.nickthecoder.paratask.project.option

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import uk.co.nickthecoder.paratask.parameter.ValueParameter
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class FileOptions(val file: File) {

    val name = file.nameWithoutExtension

    val includes = mutableListOf<String>()

    private val optionsMap = mutableMapOf<String, Option>()

    private val primaryOptionsMap = mutableMapOf<String, Option>()

    init {
        if (file.exists()) {
            load()
        }
    }

    fun listOptions(): Collection<Option> {
        return primaryOptionsMap.values
    }

    fun listIncludes(): Collection<String> {
        return includes
    }

    fun find(code: String): Option? {
        return optionsMap.get(code)
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

    /*
     Example JSON file :
 
    {
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
        val jroot = Json.parse(InputStreamReader(FileInputStream(file))).asObject()

        val jincludes = jroot.get("includes")
        jincludes?.let {
            for (jinclude in jincludes.asArray()) {
                includes.add(jinclude.asString())
            }
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
                        if (parameter is ValueParameter<*>) {
                            val jvalue = jparameter.get("value")
                            if (jvalue == null) {
                                parameter.expression = jparameter.getString("expression", "")
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
        val jroot = JsonObject()

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
                    if (parameter.expression == null) {
                        jparameter.add("value", parameter.stringValue)
                    } else {
                        jparameter.add("expression", parameter.expression ?: "")
                    }
                    jparameters.add(jparameter)
                }
                joption.add("parameters", jparameters)
            }

            joptions.add(joption)

        }
        jroot.add("options", joptions)
        BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use {
            jroot.writeTo(it, PrettyPrint.indentWithSpaces(4))
        }

    }
}
