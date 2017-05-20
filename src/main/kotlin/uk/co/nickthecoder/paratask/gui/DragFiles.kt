package uk.co.nickthecoder.paratask.gui

import java.io.File

open class DragFiles(
        val source: javafx.scene.Node,
        val modes: Array<javafx.scene.input.TransferMode> = arrayOf(javafx.scene.input.TransferMode.COPY),
        val files: () -> (List<File>?)) {

    init {
        source.setOnDragDetected { onDragDetected(it) }
        source.setOnDragDone { onDone(it) }
    }

    open fun onDragDetected(event: javafx.scene.input.MouseEvent) {
        val dragboard = source.startDragAndDrop(* modes)

        files()?.let {
            val content = javafx.scene.input.ClipboardContent()
            content.putFiles(it)
            dragboard.setContent(content)

        }
        event.consume()
    }

    open fun onDone(event: javafx.scene.input.DragEvent) {
        event.consume()
    }
}
