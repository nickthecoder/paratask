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

    override fun toString(): String {
        return "Command : ${command}"
    }

    fun createExec() = Exec(command)
}