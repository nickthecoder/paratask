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

import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.paratask.util.process.OSCommand

/**
 * Used to process operating system command line arguments, and then either run the Task directly, or start up the
 * GUI to prompt the parameters. If prompting, then ParaTaskApp is used to start JavaFX.
 *
 * Note, this can also support command line auto-completion (pressing the tab key once or twice from the bash shell).
 * Then the 1st argument must be "--autocomplete", the 2nd is the 1-based index of the argument to be completed,
 * the 3rd is the name of the program, and the remaining arguments are the "regular" arguments.
 *
 */
class TaskParser(val task: Task) {

    val metaTaskD = TaskDescription("metaTask")

    private var endOfArgs: Boolean = false

    private lateinit var arguments: List<String>

    private val promptP = BooleanParameter("prompt", oppositeName = "no-prompt", value = null,
            description = "Prompt command's arguments using a GUI")

    private val helpP = BooleanParameter("help", description = "Print this help text")

    init {
        metaTaskD.addParameters(promptP, helpP)
    }

    fun go(args: Array<String>, prompt: Boolean = false) {

        if (prompt) {
            promptP.value = true
        }

        arguments = args.toList()

        try {
            parseRegularArguments()
            if (arguments.isNotEmpty()) {
                // Some Tasks allow for a single argument at the end without the "--parameter-name" part.
                parseExtraArguments()
            }
        } catch (e: Exception) {
            // Error while parsing arguments.
            println(e)
            return
        }

        if (helpP.value == true) {
            help()
            return
        }
        runOrPrompt()
    }

    private fun runOrPrompt() {

        try {
            task.check()
        } catch (e: ParameterException) {
            if (promptP.value == false) {
                // --no-prompt specified, but task failed the checks. Exit without prompting or running
                println(e.message)
                return
            }
            // Task failed the checks, so prompt the task.
            promptTask(task)
            return
        }
        // Task passed the checks
        if (promptP.value == true) {
            promptTask(task)
        } else {
            // Run without prompting
            runTask(task)
        }
    }

    private fun promptTask(task: Task) {
        if (task is Tool) {
            ParaTaskApp.startOpenTool(task, run = false)
        } else {
            ParaTaskApp.startPromptTask(task)
        }

    }

    private fun runTask(task: Task) {
        if (task is Tool) {
            ParaTaskApp.startOpenTool(task, run = false)
        } else {
            val result = task.run()
            if (result is OSCommand) {
                val exec = Exec(result)
                exec.inheritOut()
                exec.inheritErr()
                exec.start()
                System.exit(exec.waitFor())
            }
        }
    }

    private fun parseRegularArguments() {

        while (arguments.isNotEmpty()) {

            val arg = arguments[0]
            val arg2 = if (arguments.size > 1) arguments[1] else null

            val remove = parseArgument(arg, arg2)
            arguments = arguments.slice(remove..arguments.size - 1)

            if (remove == 0 || endOfArgs) return
        }

    }

    private fun parseArgument(taskD: TaskDescription, name: String, arg2: String?): Int {
        taskD.root.find(name)?.let { parameter ->

            if (parameter is BooleanParameter && !parameter.needsValue()) {
                parameter.value = name == parameter.name
                return 1
            }


            if (arg2 == null) {
                throw ParameterException(parameter, "Expected a value for parameter $name")
            }
            if (parameter is ValueParameter<*>) {
                parameter.stringValue = arg2
                return 2
            } else {
                throw ParameterException(parameter, "Parameter $name cannot have a value")
            }
        }


        return 0
    }

    private fun parseArgument(arg: String, arg2: String?): Int {
        if (arg == "--") {
            endOfArgs = true
            return 1
        }

        if (arg.startsWith("--")) {
            val name = arg.substring(2)

            // Handle regular task parameters
            var remove = parseArgument(task.taskD, name, arg2)
            if (remove > 0) {
                return remove
            }

            // Handle meta-parameters such as --prompt
            remove = parseArgument(metaTaskD, name, arg2)
            if (remove > 0) {
                return remove
            }
            throw RuntimeException(
                    """Unknown parameter $name
NOTE, if you expect this argument to be used a "default" argument,
then place the POSIX standard "--" BEFORE the "default" argument(s).
""")
        }
        return 0
    }

    private fun parseExtraArguments() {
        task.taskD.unnamedParameter?.let { unnamedParameter ->
            if (unnamedParameter is MultipleParameter<*, *>) {
                for (arg in arguments) {
                    unnamedParameter.addStringValue(arg)
                }
                return
            }
            unnamedParameter.stringValue = arguments[0]

            if (arguments.size > 1) {
                throw RuntimeException("Unexpected extra argument ${arguments[1]}")
            }
        }
    }

    private fun helpTaskD(taskD: TaskDescription, initialMax: Int = 0): Int {
        var max = initialMax

        for (parameter in taskD.root.descendants().filter { it is ValueParameter<*> }) {
            if (parameter.name.length > max) max = parameter.name.length
            if (parameter is BooleanParameter) {
                val oppositName = parameter.oppositeName
                if (oppositName != null) {
                    if (oppositName.length > max) max = oppositName.length
                }
            }
        }

        val indent = " ".repeat(max + 9)
        val replaceNL = "\n" + indent
        val regexNL = Regex("\n")

        for (parameter in taskD.root.descendants()) {

            if (parameter is ValueParameter<*>) {
                print("    --${parameter.name}")
                print(" ".repeat(max - parameter.name.length))
                print(" : ")
                if (parameter is BooleanParameter) {
                    print("Boolean. ")
                }
                print(parameter.description.replace(regexNL, replaceNL))
                if (parameter is BooleanParameter && parameter.value == true) {
                    print(" (default)")
                }
                println()

                if (parameter is ChoiceParameter<*>) {
                    print(indent)
                    print("Possible values : ")
                    print(parameter.choiceKeys().joinToString())
                    if (parameter.value != null) {
                        print(". (default=${parameter.valueKey()})")
                    }
                    println()
                }
                if (parameter is BooleanParameter && parameter.oppositeName != null) {
                    print("    --${parameter.oppositeName}")
                    print(" ".repeat(max - parameter.oppositeName!!.length))
                    print(" : Opposite of --${parameter.name}")
                    if (parameter.value == false) {
                        print(" (default)")
                    }
                    println()
                }
            }
        }

        return max
    }

    fun help() {
        helpP.value = false

        println(task.taskD.description)

        println()
        println("Parameters")
        val max = helpTaskD(task.taskD)

        println()
        println("Additional (paratask) parameters")
        helpTaskD(metaTaskD, initialMax = max)
        println()
    }

}
