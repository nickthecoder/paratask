package uk.co.nickthecoder.paratask.util

import uk.co.nickthecoder.paratask.Tool
import java.io.File
import java.nio.file.Path

interface AutoRefreshTool : Tool, FileListener {

    override fun fileChanged(path: Path) {
        refresh()
    }

    fun refresh() {
        taskRunner.runIfNotAlready()
    }

    fun watch( file : File) {
        unwatch()
        FileWatcher.instance.register(file, this)
    }

    fun unwatch() {
        FileWatcher.instance.unregister(this)
    }

    override fun detaching() {
        unwatch()
    }
}
