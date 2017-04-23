package uk.co.nickthecoder.paratask

/**
 * How should a task check its parameters, and then run?
 * This is complex, because in a multi-threaded system, we must prevent a TaskDescriptions's parameters from changing
 * after they have been checked, and before the running Task has finished using them.
 * <p>
 * A simple approach :
 * <ol>
 * <li>Lock all of the parameters<li>
 * <li>Check the Parameters are valid</li>
 * <li>Run if the Parameters are valid</li>
 * <li>Unlock the parameters</li>
 * </ol>
 * This is what {@link SimpleTask} does, but has the down side, that can only be one running instance.
 */
interface Task {

    /**
     * @return true if the check succeded, and the task was run.
     */
    fun checkAndRun(): Boolean

    val taskD: TaskDescription
}