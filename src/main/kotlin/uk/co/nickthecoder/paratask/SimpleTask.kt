package uk.co.nickthecoder.paratask

abstract class SimpleTask<T : TaskDescription>( override val taskD: T) : Task, Runnable {

    override fun checkAndRun(): Boolean {
        taskD.root.lock()
        try {
            taskD.check() // May throw a ParameterExecption
        } catch(e: Exception) {
            taskD.root.unlock()
            return false
        }

        try {
            run()
        } finally {
            taskD.root.unlock()
        }
        return true
    }

    abstract override fun run() // From Runnable
}