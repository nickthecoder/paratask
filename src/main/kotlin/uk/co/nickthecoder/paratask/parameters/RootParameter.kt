package uk.co.nickthecoder.paratask.parameters

import uk.co.nickthecoder.paratask.TaskDescription

class RootParameter(val taskD: TaskDescription, description: String)

    : GroupParameter("root", description = description) {

    override fun findTaskD(): TaskDescription = taskD

    override fun findRoot(): RootParameter?= this

    fun valueParameters(): List<ValueParameter<*>> {
        val result = mutableListOf<ValueParameter<*>>()

        fun addAll(group: GroupParameter) {
            group.children.forEach { child ->
                if (child is ValueParameter<*>) {
                    result.add(child)
                }
                if (child is GroupParameter) {
                    addAll(child)
                }
            }
        }

        addAll(this)
        return result
    }
}
