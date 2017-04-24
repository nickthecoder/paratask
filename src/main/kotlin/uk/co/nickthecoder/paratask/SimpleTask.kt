package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameter.Values

abstract class SimpleTask() : Task {

    override fun check(values: Values) {
        taskD.root.check(values)
    }
}