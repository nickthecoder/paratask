package uk.co.nickthecoder.paratask.gui

import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode

class CompoundDropHelper(vararg helpers: DropHelper<*>) : AbstractDropHelper(modes = TransferMode.ANY) {

    val dropHelpers = helpers.toMutableList()

    override fun accept(event: DragEvent): Array<TransferMode>? {
        dropHelpers.forEach {
            val modes = it.accept(event)
            if (modes != null) {
                return modes
            }
        }
        return null
    }

    override fun onDropped(event: DragEvent): Boolean {
        dropHelpers.filter { event.dragboard.contentTypes.contains(it.dataFormat) }.firstOrNull()?.let {
            return it.onDropped(event)
        }
        return false
    }
}
