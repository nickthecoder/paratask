package uk.co.nickthecoder.paratask.gui

import java.io.File

open class DropFiles(
        val target: javafx.scene.Node,
        val source: javafx.scene.Node = target,
        val modes: Array<javafx.scene.input.TransferMode> = javafx.scene.input.TransferMode.ANY,
        val dropped: (List<File>) -> Boolean

) {

    init {
        target.setOnDragOver { onDragOver(it) }
        target.setOnDragEntered { onDragEntered(it) }
        target.setOnDragExited { onDragExited(it) }
        target.setOnDragDropped { onDragDropped(it) }
    }

    open fun accept(event: javafx.scene.input.DragEvent): Boolean {
        return event.gestureSource != source && event.dragboard.hasFiles()
    }

    open fun onDragOver(event: javafx.scene.input.DragEvent) {
        if (accept(event)) {
            event.acceptTransferModes(* modes)
        }
        event.consume()
    }

    open fun onDragEntered(event: javafx.scene.input.DragEvent) {
        if (accept(event)) {
            if (target is javafx.css.Styleable) {
                target.styleClass.add("drop")
            }
        }
        event.consume()
    }

    open fun onDragExited(event: javafx.scene.input.DragEvent) {
        if (accept(event)) {
            if (target is javafx.css.Styleable) {
                target.styleClass.remove("drop")
            }
        }
        event.consume()
    }

    open fun onDragDropped(event: javafx.scene.input.DragEvent) {
        val dragboard = event.dragboard
        var success = false
        if (accept(event)) {
            success = dropped(dragboard.files)
        }

        event.isDropCompleted = success
        event.consume()
    }

}

