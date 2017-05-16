package uk.co.nickthecoder.paratask.gui

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.project.Stoppable
import uk.co.nickthecoder.paratask.util.BufferedSink
import uk.co.nickthecoder.paratask.util.Exec
import uk.co.nickthecoder.paratask.util.ProcessListener
import java.io.PrintStream

class SimpleTerminal(val exec: Exec, showCommand: Boolean = true, allowInput: Boolean = false)

    : BorderPane(), Stoppable, ProcessListener {

    var maxSize = 100000

    private val textArea = TextArea()

    private val inputPane: BorderPane?
    private val inputField = TextField()

    private val out: PrintStream by lazy {
        PrintStream(exec.process?.outputStream)
    }

    private val submitButton: Button

    private var killed = false

    init {
        getStyleClass().add("terminal")
        textArea.getStyleClass().add("output")
        inputField.getStyleClass().add("input")

        submitButton = Button("Submit")
        submitButton.onAction = EventHandler {
            submit()
        }

        val terminateButton = Button("Terminate")
        terminateButton.onAction = EventHandler {
            exec.kill()
        }

        if (showCommand) {
            val commandLine = TextField(exec.command.toString())
            commandLine.setEditable(false)
            commandLine.getStyleClass().add("command")
            top = commandLine
        }

        center = textArea
        if (allowInput) {
            inputPane = BorderPane()
            bottom = inputPane
            inputField.requestFocus()
            inputPane.getStyleClass().add("inputArea")

            with(inputPane)
            {
                center = inputField
                left = terminateButton
                right = submitButton
            }

        } else {
            inputPane = null
            message("Running...")
        }

        with(exec)
        {
            // TODO Could show stderr in a different font/style
            mergeErrWithOut()
            outSink = TerminalSink()

            listeners.add(this@SimpleTerminal)
        }
    }

    fun start() {
        exec.start()
    }

    private fun submit() {
        out.println(inputField.text)
        out.flush()
        // TODO Color the input text differently?
        textArea.appendText("> " + inputField.text + "\n")
        textArea.selectPositionCaret(textArea.text.length)
        textArea.deselect()
        inputField.text = ""
        //textArea.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
    }

    private var focusListener: FocusListener? = null

    public fun attached() {
        if (inputPane != null) {
            focusListener = FocusListener(inputPane) { hasFocus: Boolean ->
                submitButton.setDefaultButton(hasFocus)
            }
        }
    }

    public fun detaching() {
        focusListener?.remove()
        stop()
    }

    private fun kill() {
        exec.kill(forcibly = killed) // If kill button is pressed twice, forcibly kill the second time 
        killed = true
    }

    override fun stop() {
        kill()
    }

    override fun finished(process: Process) {
        Platform.runLater {
            message("Finished : Exit status ${process.waitFor()}")
            focusListener?.remove()
        }
    }

    private fun message(message: String) {
        val textField = TextField(message)
        with(textField) {
            setEditable(false)
            getStyleClass().add("message")
            requestFocus()
            bottom = this
        }
    }

    private inner class TerminalSink : BufferedSink() {
        var appendText = StringBuilder()
        //var count = 0
        //var lines = 0
        var pendingLines = 0

        override fun sink(line: String) {
            sinkSynch(line)
            if (pendingLines > 50) {
                Thread.sleep(100)
            }
        }

        /**
         * We cannot append every line to the textarea directly, because that would flood JavaFX's thread
         * and cause the app to become unresponsive. So instead, remember what has come in, and let
         * a Platform.runLater consume a batch of lines in one go. This thread should sleep on occasion, to
         * allow the JavaFX thread to have a fair share (again, to keep the application responsive).
         */
        @Synchronized
        fun sinkSynch(line: String) {

            val empty = appendText.length == 0

            appendText.appendln(line)
            //lines++
            pendingLines++

            if (empty) {
                Platform.runLater {
                    appendText()
                }
            }
        }

        @Synchronized
        fun appendText() {
            val len = textArea.length
            if (len > maxSize) {
                textArea.deleteText(0, len - maxSize)
            }
            pendingLines = 0
            val text = appendText.toString()
            appendText = StringBuilder()
            textArea.appendText(text)
        }
    }

    fun chooseFocus(): Node? {
        return if (inputPane == null) null else inputField
    }
}