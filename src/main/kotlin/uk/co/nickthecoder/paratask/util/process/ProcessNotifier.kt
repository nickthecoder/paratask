package uk.co.nickthecoder.paratask.util.process

class ProcessNotifier {

    private val listeners = mutableListOf<ProcessListener>()

    private var ended: Boolean = false

    private lateinit var process: Process

    fun start(process: Process) {
        this.process = process

        val thread = Thread {
            process.waitFor()
            ended = true
            listeners.forEach {
                listener ->
                try {
                    listener.finished(process)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
        thread.name = "ProcessNotifier"
        thread.isDaemon = true
        thread.start()
    }

    fun add(listener: ProcessListener) {
        listeners.add(listener)
        if (ended) {
            listener.finished(process)
        }
    }

    fun remove(listener: ProcessListener) {
        listeners.remove(listener)
    }
}
