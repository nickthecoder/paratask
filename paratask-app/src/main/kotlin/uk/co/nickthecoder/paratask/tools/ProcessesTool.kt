package uk.co.nickthecoder.paratask.tools

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.util.regex.Pattern

class ProcessesTool : AbstractCommandTool<ProcessesTool.ProcessRow>() {

    override val taskD = TaskDescription("processes", description = "List Processes using the Unix 'ps' command")

    val allP = InformationParameter("all", label = "All", information = "All Processes")

    val commandP = StringParameter("command")

    val userP = StringParameter("user")

    val groupP = StringParameter("group")

    val pidsP = MultipleParameter("pids", label = "PIDs", minItems = 1, isBoxed = true) {
        IntParameter("pid")
    }

    val choiceP = OneOfParameter("filter", value = allP, choiceLabel = "Filter Type")

    override val rowFilter = RowFilter<ProcessRow>(this, columns, ProcessRow(0, "", "", 0.0, 0.0, ""))


    init {
        userP.value = System.getProperty("user.name") ?: ""

        taskD.addParameters(choiceP)
        choiceP.addParameters(allP, commandP, userP, groupP, pidsP)

        columns.add(Column<ProcessRow, Int>("pid", width = 100, getter = { it.pid }))
        columns.add(Column<ProcessRow, String>("user", width = 100, getter = { it.user }))
        columns.add(Column<ProcessRow, String>("group", width = 100, getter = { it.group }))
        columns.add(Column<ProcessRow, Double>("CPU", width = 70, label = "%CPU", getter = { it.cpu }))
        columns.add(Column<ProcessRow, Double>("memory", width = 70, label = "%Mem", getter = { it.mem }))
        columns.add(Column<ProcessRow, String>("command", width = 700, getter = { it.cmd }))
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
                command.addArguments("-C", commandP.value)
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

    private val linePattern = Pattern.compile("^([^ ]*) ++([^ ]*) ++([^ ]*) ++([^ ]*) ++([^ ]*) ++(.*)$")

    override fun processLine(line: String) {
        val matcher = linePattern.matcher(line.trim())
        if (matcher.matches()) {

            val pid = matcher.group(1).toInt()
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
            val pid: Int,
            val user: String,
            val group: String,
            val cpu: Double,
            val mem: Double,
            val cmd: String
    ) {
        fun reniceTask() = ReniceTask(pid)
    }
}

class ReniceTask() : AbstractTask() {

    constructor(pid: Int) : this() {
        pidsP.value = listOf(pid)
    }

    override val taskD = TaskDescription("renice", description = "Change process's priority")

    val priorityP = IntParameter("priority", minValue = -20, maxValue = 19, description = """0 is the default priority.
19 is the 'lowest' priority (the affected processes will run only when nothing else in the system wants to)""")

    val pidsP = MultipleParameter("pids") {
        IntParameter("pid", label = "PID")
    }

    val usersP = MultipleParameter("users") {
        IntParameter("user")
    }

    init {
        taskD.addParameters(priorityP, pidsP, usersP)
    }

    override fun run() {
        val command = OSCommand("renice", "-n", priorityP.value!!)
        if (pidsP.value.isNotEmpty()) {
            command.addArgument("--pid")
            pidsP.value.forEach {
                command.addArgument(it)
            }
        }
        if (usersP.value.isNotEmpty()) {
            command.addArgument("--user")
            usersP.value.forEach {
                command.addArgument(it)
            }
        }

        Exec(command).start().waitFor()
    }
}
