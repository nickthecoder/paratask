package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.util.HasDirectory
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.File

abstract class DirectoryResolver : ParameterResolver {

    fun resolveFile(file: File): File? {
        val path = file.path

        if (path == ".") {
            return directory()
        } else if (path == "~") {
            return homeDirectory
        } else if (path.startsWith("~" + File.separatorChar)) {
            return File(homeDirectory, path.substring(2))
        } else {
            return directory()?.resolve(file) ?: file
        }
    }

    override fun resolve(parameter: ValueParameter<*>) {

        if (parameter is FileParameter) {
            if (parameter.value == null) return
            parameter.value = resolveFile(parameter.value!!)
        }
    }

    override fun resolveValue(parameter: ValueParameter<*>, value: Any?): Any? {
        if (parameter is FileParameter) {
            if (value == null) return null
            return resolveFile(value as File)
        }
        return value
    }

    abstract fun directory(): File?
}

class PlainDirectoryResolver(val directory: File = homeDirectory) : DirectoryResolver() {
    override fun directory() = directory

    companion object {
        val instance = PlainDirectoryResolver()
    }
}

class HasDirectoryResolver(val hasDirectory: HasDirectory) : DirectoryResolver() {
    override fun directory() = hasDirectory.directory
}
