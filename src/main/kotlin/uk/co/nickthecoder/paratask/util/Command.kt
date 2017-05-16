package uk.co.nickthecoder.paratask.util

import java.io.File
import java.util.regex.Pattern

class Command(program: String, vararg args: Any?) {

    val command = mutableListOf<String>()

    val program: String
        get() = command[0]

    val arguments: List<String>
        get() = command.slice(1..command.size-1)

    var directory: File? = null

    init {
        command.add(program)
        for (arg: Any? in args) {
            if (arg != null) {
                if (arg is List<*>) {
                    arg.forEach { arg2 ->
                        if (arg2 != null) {
                            command.add(arg2.toString())
                        }
                    }
                } else {
                    command.add(arg.toString())
                }
            }
        }
    }

    fun dir(dir: File): Command {
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
