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

class SimpleTerminal(val exec: Exec, showCommand: Boolean = true, allowInput: Boolean = false)

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
        if (allowInput) {
            bottom = inputPane
        } else {
            message("Running...")
        }

        with(inputPane) {
            center = inputField
            left = terminateButton
            right = submitButton
        }

        // TODO Could show stderr in a different font/style
        with(exec) {
            mergeErrWithOut()
            outSink = TerminalSink()

            start()
            listeners.add(this@SimpleTerminal)
        }
    }

    override fun finished(process: Process) {
        Platform.runLater {
            message("Finished : Exit status ${process.waitFor()}")
        }
    }

    fun message(message: String) {
        val textField = TextField(message)
        with(textField) {
            setEditable(false)
            getStyleClass().add("message")
            requestFocus()
            bottom = this
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