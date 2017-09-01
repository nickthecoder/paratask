package uk.co.nickthecoder.paratask.tools.places

import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.tools.AbstractCommandTool
import uk.co.nickthecoder.paratask.util.Labelled
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.util.regex.Pattern

class ProcessesTool : AbstractCommandTool<ProcessesTool.ProcessRow>() {

    override val taskD = TaskDescription("processes", description = "List Processes using the Unix 'ps' command")

    val allP = InformationParameter("all", label = "All", information = "All Processes")

    val commandP = StringParameter("command", required = false)

    val userP = StringParameter("user")

    val groupP = StringParameter("group")

    val pidsP = MultipleParameter("pids", minItems = 1) {
        IntParameter("pid")
    }

    val choiceP = OneOfParameter("choiceP", value = allP)

    init {
        taskD.addParameters(choiceP)
        choiceP.addParameters(allP, userP, groupP, pidsP)
    }

    override fun createCommand(): OSCommand {
        val command = OSCommand("ps", "--no-headers", "--format", "pid user group %cpu %mem cmd")

        when (choiceP.value) {
            pidsP -> {
                pidsP.value.forEach {
                    command.addArguments("-p", it)
                }
            }

            commandP -> {
                command.addArguments("-C", commandP)
            }

            userP -> {
                command.addArguments("-U", userP.value)
            }

            groupP -> {
                command.addArguments("-G", groupP.value)
            }

            allP -> {
                command.addArgument("-e")
            }

        }
        return command
    }

    override fun createColumns(): List<Column<ProcessRow, *>> {
        val columns = mutableListOf<Column<ProcessRow, *>>()

        columns.add(Column<ProcessRow, String>("pid", width = 100) { it.pid })
        columns.add(Column<ProcessRow, String>("user", width = 100) { it.user })
        columns.add(Column<ProcessRow, String>("group", width = 100) { it.group })
        columns.add(Column<ProcessRow, Double>("CPU", width = 70, label = "%CPU") { it.cpu })
        columns.add(Column<ProcessRow, Double>("memory", width = 70, label = "%Mem") { it.mem })
        columns.add(Column<ProcessRow, String>("command", width = 700) { it.cmd })

        return columns
    }

    private val linePattern = Pattern.compile("^([^ ]*) ++([^ ]*) ++([^ ]*) ++([^ ]*) ++([^ ]*) ++(.*)$")

    override fun processLine(line: String) {
        val matcher = linePattern.matcher(line.trim())
        if (matcher.matches()) {

            val pid = matcher.group(1)
            val user = matcher.group(2)
            val group = matcher.group(3)
            val cpu = matcher.group(4).toDouble()
            val mem = matcher.group(5).toDouble()
            val cmd = matcher.group(6)

            list.add(ProcessRow(pid, user, group, cpu, mem, cmd))
        } else {
            println("Skipping Line= '$line'")
        }
    }


    data class ProcessRow(
            val pid: String,
            val user: String,
            val group: String,
            val cpu: Double,
            val mem: Double,
            val cmd: String
    )

}
