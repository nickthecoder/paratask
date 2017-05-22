package uk.co.nickthecoder.paratask.project

import javafx.beans.property.SimpleBooleanProperty
import uk.co.nickthecoder.paratask.Tool

class History(val halfTab: HalfTab) {

    private val moments = mutableListOf<Moment>()

    /*
     * The points to the moements index, at the currently active Tool/Values combo.
     * Will be -1, before a tool has ran
     * Will be 0 after a tool has ran, and placed in the list.
     * Will point to the penultimate item on the first undo.
     */
    private var index = -1

    fun canUndo(): Boolean = index > 0

    fun canRedo(): Boolean = index < moments.size - 1

    private var using = false

    val canUndoProperty = SimpleBooleanProperty(false)

    val canRedoProperty = SimpleBooleanProperty(false)

    fun update() {
        canUndoProperty.set(canUndo())
        canRedoProperty.set(canRedo())
        //dumpNamesAndValues()
    }

    private fun use(moment: Moment) {
        using = true
        val tool = moment.tool

        halfTab.changeTool(tool)
        tool.taskRunner.run()

        using = false
    }

    fun undo() {
        if (canUndo()) {
            index--
            use(moments[index])
        }

        update()
    }

    fun redo() {
        if (canRedo()) {
            index++
            use(moments[index])
        }

        update()
    }

    fun push(tool: Tool) {
        if (using) {
            return
        }

        val newMoment = Moment(tool)
        if (index >= 0) {
            if (moments[index] == newMoment) {
                return
            }
        }

        if (canRedo()) {
            for (i in moments.size - 1 downTo index + 1) {
                moments.removeAt(i)
            }
        }

        moments.add(newMoment)
        index = moments.size - 1

        update()
    }

    fun dumpNames() {
        println()
        moments.forEach { moment ->
            println("M : ${moment.creationString}")
        }
        println("Index = $index")
        println()
    }

    fun dumpNamesAndValues() {
        println()
        moments.forEach { moment ->
            println("M : ${moment.creationString}")
            println(moment.tool.taskD)
        }
        println("Index = $index")
        println()
    }

    class Moment(tool: Tool) {

        val creationString = tool.creationString()

        val values = mutableMapOf<String, String>()

        val tool: Tool
            get() {
                val result = Tool.create(creationString)
                for (parameter in result.valueParameters()) {
                    parameter.stringValue = values[parameter.name]!! // Safe
                }
                return result
            }

        init {
            for (parameter in tool.taskD.valueParameters()) {
                values.put(parameter.name, parameter.stringValue)
            }
        }

        override fun equals(other: Any?): Boolean {
            if (other is Moment) {
                return creationString == other.creationString && values == other.values
            }
            return false
        }

        override fun hashCode(): Int {
            var result = creationString.hashCode()
            result = 31 * result + values.hashCode()
            return result
        }
    }

}

