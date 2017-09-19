package uk.co.nickthecoder.paratask

object TaskFactory {

    /**
     * Maps creation strings to creation strings. The key is the alias, and the value is the actual creation string.
     * This allows tools to be renamed without breaking existing project files.
     * Used by Project.load
     */
    private val taskAliases = mutableMapOf<String, String>()

    fun addAlias(alias: String, correctName: String) {
        taskAliases[alias] = correctName
    }

    fun addAlias(alias: String, task: Task) {
        addAlias(alias, task.creationString())
    }

    fun createTask(creationString: String): Task {
        val cs = taskAliases[creationString] ?: creationString
        return Class.forName(cs).newInstance() as Task
    }

}
