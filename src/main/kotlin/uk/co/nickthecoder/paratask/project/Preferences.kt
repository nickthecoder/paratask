package uk.co.nickthecoder.paratask.project

import java.io.File
import uk.co.nickthecoder.paratask.util.*

object Preferences {

    val optionsPath = mutableListOf<File>()

    init {
        optionsPath.add(homeDirectory.child(".config", "paratask", "options"))
    }

}