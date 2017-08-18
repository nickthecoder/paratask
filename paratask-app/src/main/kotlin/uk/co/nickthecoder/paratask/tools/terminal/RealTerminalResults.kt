package uk.co.nickthecoder.paratask.tools.terminal

import com.jediterm.pty.PtyProcessTtyConnector
import com.jediterm.terminal.ui.JediTermWidget
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider
import com.pty4j.PtyProcess
import javafx.embed.swing.SwingNode
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.project.AbstractResults
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.nio.charset.Charset
import javax.swing.JComponent
import javax.swing.JPanel

class RealTerminalResults(
        tool: Tool,
        val osCommand: OSCommand,
        val showCommand: Boolean = true,
        val allowInput: Boolean = false)

    : AbstractResults(tool, "Terminal") {

    override val node = SwingNode()

    fun start() {
        node.content = createJWidget()
    }

    fun copyEnv(): MutableMap<String, String> {
        return System.getenv().toMutableMap()
    }

    fun createJWidget(): JComponent {
        val cmd = mutableListOf<String>()
        cmd.add(osCommand.program)
        cmd.addAll(osCommand.arguments)

        val env = copyEnv()
        val dir = osCommand.directory?.path

        env.put("TERM", "xterm")
        val charset = Charset.forName("UTF-8")

        val console = false

        println("Creating process : $cmd, $env, $dir, $console, $charset")
        val process = PtyProcess.exec(cmd.toTypedArray(), env, dir, console)

        val connector = PtyProcessTtyConnector(process, charset)
        val settings = DefaultSettingsProvider()
        val result = JediTermWidget(settings)
        val session = result.createTerminalSession(connector)
        session.start()
        println("Running? ${process.isRunning}");
        return result
    }

    override fun focus() {
        node.requestFocus()
    }
}
