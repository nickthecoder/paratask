package uk.co.nickthecoder.paratask.gui

import javafx.scene.input.DragEvent

class CompoundDropHelper(vararg helpers: SimpleDropHelper<*>) : AbstractDropHelper() {

    val dropHelpers = helpers.toMutableList()

    var currentHelper: SimpleDropHelper<*>? = null

    override fun onDragOver(event: DragEvent) : Boolean {
        currentHelper?.let {
            return it.onDragOver(event)
        }

        dropHelpers.forEach {
            if (it.onDragOver(event)) {
                currentHelper = it
                return true
            }
        }
        currentHelper = null
        return false
    }

    override fun onDragExited(event: DragEvent) {
        currentHelper?.onDragExited(event)
        currentHelper = null
    }

    override fun onDragDropped(event: DragEvent) {
        currentHelper?.onDragDropped(event)
        currentHelper = null
    }

}
