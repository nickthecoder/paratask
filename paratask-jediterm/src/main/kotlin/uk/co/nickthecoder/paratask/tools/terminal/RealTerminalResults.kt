package uk.co.nickthecoder.paratask.tools.terminal

import com.jediterm.pty.PtyProcessTtyConnector
import com.jediterm.terminal.ui.JediTermWidget
import com.jediterm.terminal.ui.TerminalSession
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider
import com.pty4j.PtyProcess
import javafx.embed.swing.SwingNode
import uk.co.nickthecoder.paratask.Tool
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

    override fun start(osCommand: OSCommand) {
        triedStopping = false
        SwingUtilities.invokeAndWait {
            node.content = createJWidget(osCommand)
        }
    }

    fun copyEnv(): MutableMap<String, String> {
        return System.getenv().toMutableMap()
    }

    fun createJWidget(osCommand: OSCommand): JComponent {
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
        val result = JediTermWidget(settings)
        session = result.createTerminalSession(connector)
        session?.start()
        return result
    }

    override fun waitFor(): Int {
        return process?.waitFor() ?: -12
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
        node.requestFocus()
    }
}
