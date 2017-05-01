package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.Values

interface ListTask<R> : Task {
    override fun run(values: Values): List<R>
}
