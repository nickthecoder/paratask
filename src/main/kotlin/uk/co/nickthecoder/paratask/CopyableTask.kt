package uk.co.nickthecoder.paratask

interface CopyableTask : Task {

    fun copy(): CopyableTask {

        val copy = this::class.java.newInstance()
        copy.taskD.copyValuesFrom(taskD)
        return copy
    }

    fun creationString(): String = this::class.java.name

    companion object {
        fun create(creationString: String): CopyableTask {
            return Class.forName(creationString).newInstance() as CopyableTask
        }
    }
}