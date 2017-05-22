package uk.co.nickthecoder.paratask.util

import uk.co.nickthecoder.paratask.ParaTaskApp
import java.io.File

open class WrappedFile(val file: File) {
    val icon by lazy {
        ParaTaskApp.imageResource("filetypes/${if (file.isDirectory) "directory" else "file"}.png")
    }
}