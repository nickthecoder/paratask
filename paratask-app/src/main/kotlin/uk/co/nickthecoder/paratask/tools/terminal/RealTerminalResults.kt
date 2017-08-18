package uk.co.nickthecoder.paratask.tools.terminal

import com.jediterm.pty.PtyProcessTtyConnector
import com.jediterm.terminal.ui.JediTermWidget
import com.jediterm.terminal.ui.TerminalSession
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider
import com.pty4j.PtyProcess
import javafx.embed.swing.SwingNode
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.project.AbstractResults
import uk.co.nickthecoder.paratask.util.Stoppable
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.nio.charset.Charset
import javax.swing.JComponent

class RealTerminalResults(
        tool: Tool,
        val osCommand: OSCommand,
        val showCommand: Boolean = true,
        val allowInput: Boolean = false)

    : AbstractResults(tool, "Terminal"), Stoppable {

    override val node = SwingNode()

    var process: PtyProcess? = null

    var session: TerminalSession? = null

    var triedStopping: Boolean = false

    fun start() {
        triedStopping = false
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
        process = PtyProcess.exec(cmd.toTypedArray(), env, dir, console)
        println("Created process $process")

        val connector = PtyProcessTtyConnector(process, charset)
        val settings = DefaultSettingsProvider()
        val result = JediTermWidget(settings)
        session = result.createTerminalSession(connector)
        session?.start()
        println("Running? ${process?.isRunning}");
        return result
    }

    fun waitFor(): Int {
        println("Waiting for process $process")
        return process?.waitFor() ?: -12
    }

    override fun stop() {
        println("Stopping ReakTerminalResults $process $session")
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
