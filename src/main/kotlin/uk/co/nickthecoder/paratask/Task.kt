package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.project.TaskRunner

interface Task {

    var taskRunner: TaskRunner

    val taskD: TaskDescription

    /**
     * Check that the parameters are all valid. This must be called before a task is run.
     * If Task's have their own validation, above that supplied by the Parameters themselves, then override
     * this method, and throw a ParameterException if the Parameter are not acceptable.
     * <p>
     * In most cases, this method does nothing.
     * </p>
     */
    fun check()

    /**
     * Runs the task.
     * @param values When running form a GUI, these are a COPY of the values, and therefore changing them will
     * have no effect on the values in the GUI.
     */
    fun run(): Any?
}