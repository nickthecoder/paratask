package uk.co.nickthecoder.paratask

interface TaskListener {
    fun ended(cancelled: Boolean)
}
