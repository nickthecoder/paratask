package uk.co.nickthecoder.paratask.util

import java.nio.file.Path

interface FileListener {
    fun fileChanged(path: Path)
}
