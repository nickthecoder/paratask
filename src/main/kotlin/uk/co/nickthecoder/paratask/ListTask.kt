package uk.co.nickthecoder.paratask

interface ListTask<R> : Task {
    override fun run(): List<R>
}
