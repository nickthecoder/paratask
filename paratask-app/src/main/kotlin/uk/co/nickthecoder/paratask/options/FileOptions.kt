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
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.ScriptVariables
import uk.co.nickthecoder.paratask.misc.FileListener
import uk.co.nickthecoder.paratask.misc.FileTest
import uk.co.nickthecoder.paratask.misc.FileWatcher
import java.io.*
import java.nio.file.Path

class FileOptions(override val file: File) : FileListener, FileTest {

    val name = file.nameWithoutExtension

    val includes = mutableListOf<String>()

    var comments: String = ""

    var rowFilterScript: GroovyScript? = null

    var rowClassName: String = ""
        set(v) {
            field = v
            scriptVariables.add("row", v)
        }

    var toolClassName: String = Tool::class.java.name
        set(v) {
            field = v
            scriptVariables.add("tool", v)
        }

    private val optionsMap = mutableMapOf<String, Option>()

    private val primaryOptionsMap = mutableMapOf<String, Option>()

    private var saving: Boolean = false

    val scriptVariables = ScriptVariables()

    init {
        load()
        FileWatcher.instance.register(file, this)
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
         "rowClassName" : "uk.co.nickthecoder.paratask.Foo",
         "toolClassName" : "uk.co.nickthecoder.paratask.Bar",
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
            jroot = Json.parse(InputStreamReader(file.inputStream())).asObject()
        } catch (e: Exception) {
            // We don't care if we can't read the file - it may not exist, and that's fine!
            //println("Failed to load options file ${resource}")
            return
        }

        comments = jroot.getString("comments", "")
        rowClassName = jroot.getString("rowClassName", "")
        toolClassName = jroot.getString("toolClassName", Tool::class.java.name)

        val rowFilterScriptSource = jroot.getString("rowFilterScript", "")
        rowFilterScript = if (rowFilterScriptSource == "") null else GroovyScript(rowFilterScriptSource)

        val jincludes = jroot.get("includes")
        jincludes?.let {
            jincludes.asArray().mapTo(includes) { it.asString() }
        }

        val joptions = jroot.get("options")
        joptions?.let {
            joptions.asArray().forEach { joption1 ->
                val joption = joption1.asObject()
                val option = Option.fromJson(joption)
                addOption(option)
            }
        }
    }

    fun save() {

        val jroot = JsonObject()

        if (comments.isNotBlank()) {
            jroot.add("comments", comments)
        }
        if (rowClassName.isNotBlank()) {
            jroot.add("rowClassName", rowClassName)
        }
        if (toolClassName.isNotBlank()) {
            jroot.add("toolClassName", toolClassName)
        }

        rowFilterScript?.let { jroot.add("rowFilterScript", it.source) }

        val jincludes = JsonArray()
        includes.forEach { include ->
            jincludes.add(include)
        }
        jroot.add("includes", jincludes)

        val joptions = JsonArray()
        listOptions().forEach { option ->
            joptions.add(option.toJson())
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
}
