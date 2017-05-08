package uk.co.nickthecoder.paratask.project

/**
 * Processes the result of a Task's run (but can be used in other situations too).
 */
interface ResultProcessor {

    /**
     * Return true iff the result was handled
     */
    fun process(result: Any?): Boolean
}