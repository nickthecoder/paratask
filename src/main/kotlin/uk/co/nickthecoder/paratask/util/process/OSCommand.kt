package uk.co.nickthecoder.paratask.util.process

import java.io.File

class OSCommand(program: String, vararg args: Any?) {

    val command = mutableListOf<String>()

    val program: String
        get() = command[0]

    val arguments: List<String>
        get() = command.slice(1..command.size - 1)

    var directory: File? = null

    init {
        command.add(program)
        args.filterNotNull().forEach {
            if (it is List<*>) {
                it.forEach { arg2 ->
                    if (arg2 != null) {
                        command.add(arg2.toString())
                    }
                }
            } else {
                command.add(it.toString())
            }
        }
    }

    fun dir(dir: File): OSCommand {
        directory = dir
        return this
    }

    fun addArgument(arg: Any?) {
        if (arg != null) {
            command.add(arg.toString())
        }
    }

    fun addArguments(vararg args: Any?) {
        args.filter { it != null }.forEach { addArgument(it) }
    }

    override fun toString(): String = command.map { escapeArg(it) }.joinToString(separator = " ")

    companion object {

        fun escapeArg(arg: String): String {
            for (c in " *()%!&<>&|'\"") {
                if (arg.contains(c)) {
                    return "'" + arg.replace(Regex("//"), "////").replace(Regex("'"), "//'") + "'"
                }
            }
            return arg
        }
    }
}
