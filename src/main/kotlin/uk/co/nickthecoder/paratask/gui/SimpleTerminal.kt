/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask.gui

import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.util.process.BufferedSink
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.paratask.util.process.ProcessListener
import java.io.PrintStream

class SimpleTerminal(val exec: Exec, showCommand: Boolean = true, allowInput: Boolean = false)

    : BorderPane(), Stoppable, ProcessListener, FocusListener {

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
        styleClass.add("terminal")
        textArea.styleClass.add("output")
        inputField.styleClass.add("input")

        submitButton = Button("Submit")
        submitButton.onAction = EventHandler {
            submit()
        }

        val terminateButton = Button("Terminate")
        terminateButton.onAction = EventHandler {
            exec.kill()
        }

        if (showCommand) {
            val commandLine = TextField(exec.osCommand.toString())
            commandLine.isEditable = false
            commandLine.styleClass.add("osCommand")
            top = commandLine
        }

        center = textArea
        if (allowInput) {
            inputPane = BorderPane()
            bottom = inputPane
            inputField.requestFocus()
            inputPane.styleClass.add("inputArea")

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
        textArea.appendText("> " + inputField.text + "\n")
        textArea.selectPositionCaret(textArea.text.length)
        textArea.deselect()
        inputField.text = ""
        //textArea.setScrollTop(Double.MAX_VALUE); //this will scroll to the bottom
    }

    private var focusHelper: FocusHelper? = null

    fun attached() {
        if (inputPane != null) {
            focusHelper = FocusHelper(inputPane, this, name = "SimpleTerminal")
        }
    }

    override fun focusChanged(gained: Boolean) {
        submitButton?.setDefaultButton(gained)
    }

    fun detaching() {
        focusHelper?.remove()
        stop()
    }

    fun focus() {
        if (bottom === inputPane) {
            inputField.requestFocus()
        } else {
            textArea.requestFocus()
        }
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
            focusHelper?.remove()
        }
    }

    private fun message(message: String) {
        val textField = TextField(message)
        with(textField) {
            isEditable = false
            styleClass.add("message")
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

            val empty = appendText.isEmpty()

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

}
