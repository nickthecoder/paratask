package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.TransferMode
import java.io.File

open class DragFiles(
        val source: Node,
        val modes: Array<TransferMode> = arrayOf(TransferMode.COPY),
        val files: () -> (List<File>?)) {

    init {
        source.setOnDragDetected { onDragDetected(it) }
        source.setOnDragDone { onDone(it) }
    }

    open fun onDragDetected(event: MouseEvent) {
        val dragboard = source.startDragAndDrop(* modes)

        files()?.let {
            val content = ClipboardContent()
            content.putFiles(it)
            dragboard.setContent(content)

        }
        event.consume()
    }

    open fun onDone(event: DragEvent) {
        event.consume()
    }
}
