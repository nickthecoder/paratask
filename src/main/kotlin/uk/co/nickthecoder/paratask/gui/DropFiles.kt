package uk.co.nickthecoder.paratask.gui

import javafx.css.PseudoClass
import javafx.css.Styleable
import javafx.scene.Node
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import java.io.File

open class DropFiles(
        val target: javafx.scene.Node,
        val source: javafx.scene.Node = target,
        val modes: Array<javafx.scene.input.TransferMode> = javafx.scene.input.TransferMode.ANY,
        val dropped: (List<java.io.File>) -> Boolean

) {

    init {
        target.setOnDragOver { onDragOver(it) }
        target.setOnDragEntered { onDragEntered(it) }
        target.setOnDragExited { onDragExited(it) }
        target.setOnDragDropped { onDragDropped(it) }
    }

    open fun accept(event: javafx.scene.input.DragEvent): Boolean {
        return event.getGestureSource() != source && event.getDragboard().hasFiles()
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
                target.getStyleClass().add("drop")
            }
        }
        event.consume()
    }

    open fun onDragExited(event: javafx.scene.input.DragEvent) {
        if (accept(event)) {
            if (target is javafx.css.Styleable) {
                target.getStyleClass().remove("drop")
            }
        }
        event.consume()
    }

    open fun onDragDropped(event: javafx.scene.input.DragEvent) {
        val dragboard = event.getDragboard()
        var success = false;
        if (accept(event)) {
            success = dropped(dragboard.getFiles())
        }

        event.setDropCompleted(success)
        event.consume()
    }

}

