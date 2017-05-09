package uk.co.nickthecoder.paratask.util

import java.io.File

class Command(program: String, vararg args: Any?) {

    val command = mutableListOf<String>()

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

    override fun toString(): String =
            if (command.size > 1) {
                command[0] + command.slice(1..command.size - 1).joinToString(prefix = " '", postfix = "'", separator = "' '")
            } else {
                command[0]
            }
}