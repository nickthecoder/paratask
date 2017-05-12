package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.parameter.ChoiceParameter
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.File

object Preferences {

    val optionsPath = mutableListOf<File>()

    var projectsDirectory = homeDirectory.child(".config", "paratask", "projects")

    init {
        optionsPath.add(homeDirectory.child(".config", "paratask", "options"))
    }

    fun createOptionsDirectoryParameter(
            name: String = "directory",
            required: Boolean = false,
            defaultFirst: Boolean = required
    ): ChoiceParameter<File?> {

        val result = ChoiceParameter<File?>(name, value = null, required = required)
        if (!required) {
            result.choice("", null, "<ALL>")
        }

        for (directory in optionsPath) {
            result.choice(directory.name, directory, directory.getPath())
            if (defaultFirst && result.value == null) {
                result.value = directory
            }
        }
        return result
    }
}