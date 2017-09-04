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
import javafx.scene.input.DataFormat
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.util.JsonHelper
import java.io.Externalizable
import java.io.ObjectInput
import java.io.ObjectOutput

interface Option : Externalizable, Comparable<Option> {

    var code: String

    var aliases: MutableList<String>

    var label: String

    var isRow: Boolean

    var isMultiple: Boolean

    var refresh: Boolean

    var newTab: Boolean

    var prompt: Boolean

    var shortcut: KeyCodeCombination?

    fun run(tool: Tool, row: Any): Any?

    fun runNonRow(tool: Tool): Any?

    fun runMultiple(tool: Tool, rows: List<Any>): Any?

    fun copy(): Option

    fun toJson(): JsonObject {
        val joption = JsonObject()

        when (this) {
            is GroovyOption -> {
                joption.set("type", "groovy")
                joption.set("script", script)
            }
            is TaskOption -> {
                joption.set("type", "task")
                joption.set("task", task.creationString())
            }
            else -> {
                throw RuntimeException("Unknown Option : ${javaClass}")
            }
        }
        with(joption) {
            set("code", code)
            set("label", label)
            set("isRow", isRow)
            set("isMultiple", isMultiple)
            set("prompt", prompt)
            set("newTab", newTab)
            set("refresh", refresh)

            shortcut?.let {
                set("keyCode", it.code.toString())
                saveModifier(joption, "shift", it.shift)
                saveModifier(joption, "control", it.control)
                saveModifier(joption, "alt", it.alt)
            }
        }


        if (aliases.size > 0) {
            val jaliases = JsonArray()
            for (alias in aliases) {
                jaliases.add(alias)
            }
            joption.add("aliases", jaliases)
        }
        if (this is TaskOption) {
            val jparameters = JsonHelper.parametersAsJsonArray(task)
            joption.add("parameters", jparameters)
        }
        return joption
    }

    override fun readExternal(input: ObjectInput) {
        val jsonString = input.readObject() as String
        val jsonObject = Json.parse(jsonString) as JsonObject
        val option = fromJson(jsonObject)

        code = option.code
        aliases = option.aliases
        label = option.label
        isRow = option.isRow
        isMultiple = option.isMultiple
        refresh = option.refresh
        newTab = option.newTab
        prompt = option.prompt
        shortcut = option.shortcut
        if (this is TaskOption && option is TaskOption) {
            task = option.task
        }
        if (this is GroovyOption && option is GroovyOption) {
            script = option.script
        }
    }

    override fun writeExternal(out: ObjectOutput) {
        val jsonString = toJson().toString()
        out.writeObject(jsonString)
    }

    companion object {
        val dataFormat = DataFormat("application/x-java-paratask-option-list")

        private fun saveModifier(joption: JsonObject, name: String, mod: KeyCombination.ModifierValue) {
            if (mod != KeyCombination.ModifierValue.UP) {
                joption.set(name, mod.toString())
            }
        }

        fun fromJson(joption: JsonObject): Option {
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
                JsonHelper.read(jparameters, option.task)
            }
            return option
        }
    }

    override fun compareTo(other: Option): Int {
        return this.label.compareTo(other.label)
    }
}
