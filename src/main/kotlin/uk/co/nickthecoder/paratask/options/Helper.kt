package uk.co.nickthecoder.paratask.options

import uk.co.nickthecoder.paratask.project.Preferences
import uk.co.nickthecoder.paratask.util.homeDirectory

/**
 * Helper class for groovy code to use, mostly to access kotlin code that is otherwise difficult to access from groovy.
 */
class Helper() {
    companion object {
        val instance = Helper()
    }

    val home = homeDirectory

    val configDirectory = Preferences.projectsDirectory.parentFile
}