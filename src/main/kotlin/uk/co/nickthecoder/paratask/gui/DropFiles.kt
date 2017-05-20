package uk.co.nickthecoder.paratask.gui

import javafx.css.Styleable
import javafx.scene.input.TransferMode
import javafx.scene.Node
import javafx.scene.input.DragEvent
import java.io.File

open class DropFiles(
        val target: Node,
        val source: Node = target,
        val modes: Array<TransferMode> = TransferMode.ANY,
        val dropped: (List<File>) -> Boolean

) {

    init {
        target.setOnDragOver { onDragOver(it) }
        target.setOnDragEntered { onDragEntered(it) }
        target.setOnDragExited { onDragExited(it) }
        target.setOnDragDropped { onDragDropped(it) }
    }

    open fun accept(event: DragEvent): Boolean {
        return event.gestureSource != source && event.dragboard.hasFiles()
    }

    open fun onDragOver(event: DragEvent) {
        if (accept(event)) {
            event.acceptTransferModes(* modes)
        }
        event.consume()
    }

    open fun onDragEntered(event: DragEvent) {
        if (accept(event)) {
            if (target is Styleable) {
                target.styleClass.add("drop")
            }
        }
        event.consume()
    }

    open fun onDragExited(event: DragEvent) {
        if (accept(event)) {
            if (target is Styleable) {
                target.styleClass.remove("drop")
            }
        }
        event.consume()
    }

    open fun onDragDropped(event: DragEvent) {
        val dragboard = event.dragboard
        var success = false
        if (accept(event)) {
            success = dropped(dragboard.files)
        }

        event.isDropCompleted = success
        event.consume()
    }

}

