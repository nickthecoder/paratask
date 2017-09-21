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
package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameters.ChoiceParameter

/**
 * Prints usage information for the ParaTaskApp main method.
 */
class HelpTask : AbstractTask() {

    override val taskD = TaskDescription("help", description = "Displays the help message")

    val helpTypeP = ChoiceParameter("helpType", required = false, value = "usage")

    init {
        helpTypeP.addChoice("usage", "usage")
        helpTypeP.addChoice("tasks", "tasks")
        helpTypeP.addChoice("classes", "classes")

        taskD.addParameters(helpTypeP)
        taskD.unnamedParameter = helpTypeP
    }

    override fun run() {

        if (helpTypeP.value == "tasks") {
            printTaskNames()
        } else if (helpTypeP.value == "classes") {
            printTaskClasses()
        } else {
            printUsage()
        }
    }

    fun printUsage() {

        println("Usage :")
        println()
        println("To RUN a task :")
        println("   paratask TASK_ID [TASK_ARGUMENTS...]")
        println("       Where IASK_ID can be the name of a task, or its fully qualified java class name.")
        println("       Note. If the arguments are incomplete or invalid, then a GUI will appear allowing you to correct the arguments.")
        println("       To prevent the GUI appearing, you can use the '--no-prompt' option :")
        println("   paratask TASK_ID --no-prompt [TASK_ARGUMENTS...]")
        println()
        println("To PROMPT a task :")
        println("   paratask TASK_ID --prompt [TASK_ARGUMENTS...]")
        println()
        println("For HELP with a particular task :")
        println("   paratask TASK_ID --help")
        println()
        println("For a LIST of task NAMES :")
        println("   paratask --help tasks")
        println()
        println("For a LIST of task CLASS NAMES :")
        println("   paratask --help classes")
        println()
    }

    fun printTaskNames() {
        TaskRegistry.allTasks().sortedBy { it.taskD.name }.forEach { task ->
            println("    ${task.taskD.name} : ${task.taskD.description}")
        }
    }

    fun printTaskClasses() {
        TaskRegistry.allTasks().sortedBy { it.creationString() }.forEach { task ->
            println("    ${task.creationString()} : ${task.taskD.description}")
        }
    }

}
