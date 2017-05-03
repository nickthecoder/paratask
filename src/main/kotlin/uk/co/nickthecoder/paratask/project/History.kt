package uk.co.nickthecoder.paratask.project

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

    private fun use(moment: Moment) {
        println("History.using")
        using = true
        halfTab.changeTool(moment.tool)
        halfTab.toolPane.values = moment.values
        halfTab.toolPane.parametersPane.run()
        using = false
    }

    fun undo() {
        println("History.undo")

        if (canUndo()) {
            index--
            use(moments[index])
        }

        dumpNames()
    }

    fun redo() {
        println("History.redo")

        if (canRedo()) {
            index++
            use(moments[index])
        }

        dumpNames()
    }

    fun push(tool: Tool, values: Values) {
        if (using) {
            println("Ignoring, as this is ME using an item in history")
            return
        }

        println("History push")

        val newMoment = Moment(tool, values)
        if (index >= 0) {
            if (moments[index] == newMoment) {
                // Same tool and values, so no need to remember
                println("History. Ignoring duplicate moment")
                return
            }
        }
        println("History. Not the same moment")

        if (canRedo()) {
            for (i in moments.size - 1 downTo index + 1) {
                println("History. Removing moment")
                moments.removeAt(i)
            }
        }
        println("History. adding moment")

        moments.add(newMoment)
        index = moments.size - 1

        dumpNames()
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
                println("Moments same ? ${creationString} vs ${other.creationString}")
                return creationString == other.creationString && values == other.values
            }
            println( "Not a moment")
            return false
        }
    }

}

