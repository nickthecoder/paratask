package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.util.HasDirectory
import java.io.File

abstract class DirectoryResolver : ParameterResolver {

    override fun resolve(parameter: ValueParameter<*>) {
        if (parameter is FileParameter) {
            if (parameter.value?.path == ".") {
                parameter.value = directory()
            } else {
                parameter.value?.let { parameter.value = directory()?.resolve(it) ?: it }
            }
        }
    }

    abstract fun directory(): File?
}

class HasDirectoryResolver(val hasDirectory: HasDirectory) : DirectoryResolver() {
    override fun directory() = hasDirectory.directory
}