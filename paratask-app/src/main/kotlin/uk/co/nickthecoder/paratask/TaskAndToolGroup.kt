package uk.co.nickthecoder.paratask

class TaskAndToolGroup(label: String) : TaskGroup(label) {

    private val tools = mutableListOf<Tool>()

    fun listTasksNotTools() = super.listTasks()

    fun listTools() = tools.map { it.copy() }

    override fun listTasks() = listTools() + listTasksNotTools()

    override fun addTasks(vararg tasks: Task) {
        super.addTasks(*tasks)
        tools.forEach {
            if (it is Registers) {
                TaskRegistry.register(it)
            }
        }
    }

    fun addTools(vararg tools: Tool) {
        this.tools.addAll(tools)
        tools.forEach {
            if (it is Registers) {
                TaskRegistry.register(it)
            }
        }
    }

}