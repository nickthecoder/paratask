package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.util.HasDirectory
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.File

abstract class DirectoryResolver : ParameterResolver {

    override fun resolve(parameter: ValueParameter<*>) {
        if (parameter.value == null) return

        if (parameter is FileParameter) {
            val path = parameter.value!!.path

            parameter.value = if (path == ".") {
                directory()
            } else if (path == "~") {
                homeDirectory
            } else if (path.startsWith("~" + File.separatorChar)) {
                File(homeDirectory, path.substring(2))
            } else {
                directory()?.resolve(parameter.value!!)
            }
        }
    }

    abstract fun directory(): File?
}

class HasDirectoryResolver(val hasDirectory: HasDirectory) : DirectoryResolver() {
    override fun directory() = hasDirectory.directory
}