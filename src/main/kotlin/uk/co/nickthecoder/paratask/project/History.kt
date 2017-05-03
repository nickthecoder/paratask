package uk.co.nickthecoder.paratask.project

import javafx.beans.property.SimpleBooleanProperty
import uk.co.nickthecoder.paratask.gui.project.HalfTab
import uk.co.nickthecoder.paratask.parameter.Values

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
        //dumpNames()   
    }

    private fun use(moment: Moment) {
        using = true
        val tool = moment.tool
        tool.autoRun = true

        halfTab.changeTool(tool, moment.values)

        using = false
    }

    fun undo() {
        println("History.undo")

        if (canUndo()) {
            index--
            use(moments[index])
        }

        update()
    }

    fun redo() {
        //println("History.redo")

        if (canRedo()) {
            index++
            use(moments[index])
        }

        update()
    }

    fun push(tool: Tool, values: Values) {
        if (using) {
            return
        }

        val newMoment = Moment(tool, values)
        if (index >= 0) {
            if (moments[index] == newMoment) {
                return
            }
        }

        if (canRedo()) {
            for (i in moments.size - 1 downTo index + 1) {
                println("History. Removing moment")
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
        println("Index = ${index}")
        println()
    }

    class Moment(tool: Tool, values: Values) {

        val creationString = tool.creationString()

        val tool: Tool
            get() = Tool.create(creationString)

        val values: Values = values.copy()

        override fun equals(other: Any?): Boolean {
            if (other is Moment) {
                return creationString == other.creationString && values == other.values
            }
            return false
        }
    }

}

