package uk.co.nickthecoder.paratask.util

import uk.co.nickthecoder.paratask.ParaTaskApp
import java.io.File

class WrappedFile(override val file: File) : HasFile {
    val icon by lazy {
        ParaTaskApp.imageResource("filetypes/${if (file.isDirectory()) "directory" else "file"}.png")
    }
}