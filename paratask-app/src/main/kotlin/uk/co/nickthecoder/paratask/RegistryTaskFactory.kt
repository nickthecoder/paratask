package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameters.TaskFactory

class RegisteredTaskFactory : TaskFactory {

    override val creationStringToTask = mutableMapOf<String, Task>()

    override val topLevelTasks: List<Task>

    override val taskGroups: List<TaskAndToolGroup> = TaskRegistry.listGroups().filter { it != TaskRegistry.topLevel }

    init {
        TaskRegistry.listGroups().forEach { group ->
            group.listTasks().forEach { task ->
                creationStringToTask.put(task.creationString(), task)
            }
        }

        val list = mutableListOf<Task>()
        list.addAll(TaskRegistry.topLevel.listTools())
        list.addAll(TaskRegistry.topLevel.listTasks())
        topLevelTasks = list
    }

    companion object {
        val instance = RegisteredTaskFactory()
    }
}
