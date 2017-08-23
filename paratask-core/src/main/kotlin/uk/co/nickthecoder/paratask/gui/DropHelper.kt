package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.input.DragEvent

interface DropHelper {

    fun applyTo(target: Node): DropHelper

    fun unapplyTo(target: Node)

    fun exclude(node: Node): DropHelper

    fun cancel()

    fun onDragOver(event: DragEvent) : Boolean
    fun onDragExited(event: DragEvent)
    fun onDragDropped(event: DragEvent)
}
