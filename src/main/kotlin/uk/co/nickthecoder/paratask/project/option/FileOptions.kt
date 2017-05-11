package uk.co.nickthecoder.paratask.project.option

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import uk.co.nickthecoder.paratask.util.HasFile
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class FileOptions(override val file: File) : HasFile {

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
        primaryOptionsMap.filterValues { it !== option }
        optionsMap.filterValues { it !== option }
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
                "code" : "2",
                "label" : "Two",
                "script" : "",
                "isRow" : false,
                "isMultiple" : false,
                "newTab" : false,
                "prompt" : false,
                "refreshResults" : true
             },
             {
                "type" : "groovy",
                "code" : "5",
                "label" : "Five",
                "script" : "",
                "isRow" : false,
                "isMultiple" : false,
                "newTab" : false,
                "prompt" : false,
                "refreshResults" : true
             }
         ] 

    }
     */
    // https://github.com/ralfstx/minimal-json
    fun load() {
        println("Loading ${file}")
        val jroot = Json.parse(InputStreamReader(FileInputStream(file))).asObject()

        val jincludes = jroot.get("include")
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
                        option = GroovyOption()
                    }
                    else -> {
                        throw RuntimeException("Unknown option type : " + type)
                    }
                }

                with(option) {
                    code = joption.getString("code", "?")
                    label = joption.getString("label", "")
                    script = joption.getString("script", "")
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
                addOption(option)
                println("Added option ${option}")
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
        for ((_, option) in optionsMap) {
            val joption = JsonObject()

            when (option) {
                is GroovyOption -> {
                    joption.set("type", "groovy")
                }
                else -> {
                    throw RuntimeException("Unknown Option : ${option.javaClass}")
                }
            }
            with(joption) {
                set("code", option.code)
                set("label", option.label)
                set("script", option.script)
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

            joptions.add(joption)

        }
        jroot.add("options", joptions)
        BufferedWriter(OutputStreamWriter(FileOutputStream(file))).use {
            jroot.writeTo(it, PrettyPrint.indentWithSpaces(4))
        }

    }
}
