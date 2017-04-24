package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.Values

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
     * Check that the parameters are all valid. This must be called before a task is run.
     * If Task's have their own validation, above that supplied by the Parameters themselves, then override
     * this method, and throw a ParameterException if the Parameter are not acceptable.
     * <p>
     * In most cases, this method doesn't need to be overridden.
     * </p>
     */
    fun check(values: Values)

    /**
     */
    fun run(values: Values)

    val taskD: TaskDescription
}