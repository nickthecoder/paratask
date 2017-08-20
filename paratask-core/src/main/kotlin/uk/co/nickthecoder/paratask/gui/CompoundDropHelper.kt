package uk.co.nickthecoder.paratask.gui

import javafx.css.Styleable
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode

class CompoundDropHelper(vararg helpers: DropHelper<*>) : AbstractDropHelper() {

    val dropHelpers = helpers.toMutableList()

    var currentHelper: DropHelper<*>? = null

    override fun onDragOver(event: DragEvent): Boolean {
        dropHelpers.forEach {
            if (it.onDragOver(event) == true) {
                currentHelper = it
                return true
            }
        }
        currentHelper = null
        return false
    }


    override fun onDragExited(event: DragEvent) {
        currentHelper?.let {
            it.onDragExited(event)
        }
        currentHelper = null
    }

    override fun onDragDropped(event: DragEvent) {
        currentHelper?.let {
            it.onDragDropped(event)
        }
        currentHelper = null
    }

}
