package uk.co.nickthecoder.paratask.project.option

import uk.co.nickthecoder.paratask.project.option.Option
import java.io.File

object OptionsManager {

    private val topLevelMap = mutableMapOf<String, TopLevelOptions>()

    private fun getTopLevelOptions(optionsName: String): TopLevelOptions {
        val found = topLevelMap.get(optionsName)
        if (found == null) {
            val newTL = TopLevelOptions(optionsName)
            topLevelMap.put(optionsName, newTL)
            return newTL
        } else {
            return found
        }
    }

    fun find(code: String, optionsName: String): Option? {
        return getTopLevelOptions(optionsName).find(code)
    }

    private val pathMap = mutableMapOf<File, OptionsPath>()

    private fun getOptionsPath(directory: File): OptionsPath {
        val found = pathMap.get(directory)
        if (found == null) {
            val newOP = OptionsPath(directory)
            pathMap.put(directory, newOP)
            return newOP
        } else {
            return found
        }
    }

    fun getFileOptions(optionsName: String, directory: File): FileOptions {
        return getOptionsPath(directory).getFileOptions(optionsName)
    }
}

data class OptionsPath(val directory: File) {

    val fileOptionsMap = mutableMapOf<String, FileOptions>()

    fun getFileOptions(optionsName: String): FileOptions {
        return FileOptions(File(directory, optionsName + ".json"))
    }
}
