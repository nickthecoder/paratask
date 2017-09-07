/*
ParaTask Copyright (C) 2017  Nick Robinson>

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
package uk.co.nickthecoder.paratask.tools.terminal

import com.jediterm.pty.PtyProcessTtyConnector
import com.jediterm.terminal.ui.JediTermWidget
import com.jediterm.terminal.ui.TerminalSession
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider
import com.pty4j.PtyProcess
import javafx.application.Platform
import javafx.embed.swing.SwingNode
import javafx.scene.input.DataFormat
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.gui.CompoundDropHelper
import uk.co.nickthecoder.paratask.gui.DropFiles
import uk.co.nickthecoder.paratask.gui.DropHelper
import uk.co.nickthecoder.paratask.gui.SimpleDropHelper
import uk.co.nickthecoder.paratask.project.AbstractResults
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.nio.charset.Charset
import javax.swing.JComponent
import javax.swing.SwingUtilities

class RealTerminalResults(tool: Tool)

    : AbstractResults(tool, "Terminal"), TerminalResults {

    override val node = SwingNode()

    override var process: PtyProcess? = null

    var session: TerminalSession? = null

    var triedStopping: Boolean = false

    var termWidget: JediTermWidget? = null

    val textDropHelper = SimpleDropHelper<String>(DataFormat.PLAIN_TEXT, arrayOf(TransferMode.COPY)) { event, text ->
        sendText(text)
    }
    val filesDropHelper = DropFiles(arrayOf(TransferMode.COPY)) { event, files ->
        val text = files.map { quoteFilenameIfNeeded(it.path) }.joinToString(separator = " ")
        sendText(text)
    }
    val compoundDropHelper = CompoundDropHelper(filesDropHelper, textDropHelper)

    fun quoteFilenameIfNeeded(filename: String): String {
        if (filename.matches(Regex("[a-zA-Z0-9,._+:@%/-]*"))) {
            return filename
        }
        return "'" + filename.replace("//", "////").replace("'", "'\\''") + "'"
    }

    override fun start(osCommand: OSCommand) {
        triedStopping = false
        SwingUtilities.invokeAndWait {
            node.content = createJWidget(osCommand)
        }
    }

    fun copyEnv(): MutableMap<String, String> {
        return System.getenv().toMutableMap()
    }


    fun createJWidget(osCommand: OSCommand): JediTermWidget {
        val cmd = mutableListOf<String>()
        cmd.add(osCommand.program)
        cmd.addAll(osCommand.arguments)

        val env = copyEnv()
        val dir = osCommand.directory?.path

        env.put("TERM", "xterm")
        val charset = Charset.forName("UTF-8")

        val console = false

        process = PtyProcess.exec(cmd.toTypedArray(), env, dir, console)

        val connector = PtyProcessTtyConnector(process, charset)
        val settings = DefaultSettingsProvider()
        termWidget = JediTermWidget(settings)
        session = termWidget!!.createTerminalSession(connector)
        session?.start()

        compoundDropHelper.applyTo(node)

        return termWidget!!
    }

    fun sendText(text: String) {
        termWidget?.terminalStarter?.sendString(text)

    }

    override fun waitFor(): Int {
        val exitStatus = process?.waitFor() ?: -12
        Platform.runLater {
            labelProperty.set("Finished (Exit Status=$exitStatus)")
        }
        return exitStatus
    }

    override fun detaching() {
        node.content = null
        super.detaching()
        stop()
    }

    override fun stop() {
        if (triedStopping) {
            process?.destroyForcibly()
        } else {
            process?.destroy()
        }
        session?.close()
        triedStopping = true
    }

    override fun focus() {
        ParaTaskApp.logFocus("RealTerminalResults.focus. node.requestFocus()")
        node.requestFocus()
    }
}
