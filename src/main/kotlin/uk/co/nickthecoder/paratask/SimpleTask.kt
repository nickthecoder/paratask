package uk.co.nickthecoder.paratask

abstract class SimpleTask() : Task, Runnable {

    override fun check() {
        taskD.root.check()
    }

    override fun checkAndRun(): Boolean {
        taskD.root.lock()
        try {
            check() // May throw a ParameterExecption
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