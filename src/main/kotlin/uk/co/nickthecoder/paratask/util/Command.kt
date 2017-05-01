package uk.co.nickthecoder.paratask.util

class Command(vararg args: Any?) {

    val command = mutableListOf<String>()

    init {
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