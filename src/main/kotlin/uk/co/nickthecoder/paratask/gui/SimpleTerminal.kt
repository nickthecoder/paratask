package uk.co.nickthecoder.paratask.gui

import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.util.BufferedSink
import uk.co.nickthecoder.paratask.util.Exec
import uk.co.nickthecoder.paratask.util.ProcessListener

class SimpleTerminal(val exec: Exec, showCommand: Boolean = true)

    : BorderPane(), ProcessListener {

    val textArea = TextArea()

    val inputPane = BorderPane()

    val inputField = TextField()

    init {
        getStyleClass().add("terminal")
        textArea.getStyleClass().add("output")
        inputPane.getStyleClass().add("inputArea")
        inputField.getStyleClass().add("input")

        textArea.setEditable(false);

        val submitButton = Button("Submit")
        // TODO Implement Submit button

        val terminateButton = Button("Terminate")
        // TODO Implement Terminate button

        if (showCommand) {
            val commandLine = TextField(exec.command.toString())
            commandLine.setEditable(false)
            commandLine.getStyleClass().add("command")
            top = commandLine
        }

        center = textArea
        bottom = inputPane

        inputPane.center = inputField
        inputPane.left = terminateButton
        inputPane.right = submitButton

        // TODO Could show stderr in a different font/style
        exec.mergeErrWithOut()
        exec.outSink = TerminalSink()

        exec.start()
        exec.listeners.add(this)
    }

    override fun finished(process: Process) {
        Platform.runLater {
            val finished = TextField("Finished : Exit status ${process.waitFor()}")
            finished.setEditable(false)
            finished.getStyleClass().add("finished")
            bottom = finished
            finished.requestFocus()
        }
    }

    private inner class TerminalSink : BufferedSink() {
        override fun sink(line: String) {
            Platform.runLater {
                textArea.appendText(line)
                textArea.appendText("\n")
            }
        }
    }
}