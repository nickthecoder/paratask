package uk.co.nickthecoder.paratask.project.option

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import uk.co.nickthecoder.paratask.util.HasFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class FileOptions(override val file: File) : HasFile {

    val name = file.nameWithoutExtension

    private val includes = mutableListOf<String>()

    // TODO When I implement "if" clauses, this will be a map of <String, List<Options>>
    private val optionsMap = mutableMapOf<String, Option>()

    init {
        if (file.exists()) {
            load()
        }
    }

    fun listOptions(): Collection<Option> {
        return optionsMap.values
    }


    fun listIncludes(): Collection<String> {
        return includes
    }

    fun find(code: String): Option? {
        return optionsMap.get(code)
    }

    fun renameOption(option: Option, newCode: String) {
        removeOption(option)
        option.code = newCode
        addOption(option)
    }

    fun addOption(option: Option) {
        optionsMap.put(option.code, option)
    }

    fun removeOption(option: Option) {
        optionsMap.remove(option.code)
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

                when (type) {

                    "groovy" -> {
                        val option = GroovyOption(
                                code = joption.getString("code", "?"),
                                label = joption.getString("label", "<no label>"),
                                script = joption.getString("script", ""),
                                isRow = joption.getBoolean("isRow", false),
                                isMultiple = joption.getBoolean("isMultiple", false),
                                newTab = joption.getBoolean("newTab", false),
                                prompt = joption.getBoolean("prompt", false),
                                refresh = joption.getBoolean("refresh", false)
                        )
                        addOption(option)
                    }

                    else -> {
                        throw RuntimeException("Unknown option type : " + type)
                    }
                }
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
            when (option) {
                is GroovyOption -> {
                    val joption = JsonObject()
                    joption.set("type", "groovy")
                    joption.set("code", option.code)
                    joption.set("label", option.label)
                    joption.set("script", option.script)
                    joption.set("isRow", option.isRow)
                    joption.set("isMultiple", option.isMultiple)
                    joption.set("prompt", option.prompt)
                    joption.set("newTab", option.newTab)
                    joption.set("refresh", option.refresh)

                    joptions.add(joption)
                }
                else -> {
                    throw RuntimeException("Unknown Option : ${option.javaClass}")
                }
            }
            jroot.add("options", joptions)

            jroot.writeTo(OutputStreamWriter(FileOutputStream(file)))
        }

    }
}
