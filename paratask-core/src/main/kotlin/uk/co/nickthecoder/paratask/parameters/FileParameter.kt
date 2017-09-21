/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask.parameters

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.parameters.fields.FileField
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.currentDirectory
import uk.co.nickthecoder.paratask.util.uncamel
import java.io.File

open class FileParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        required: Boolean = true,
        val expectFile: Boolean? = true, // false=expect Directory, null=expect either
        val mustExist: Boolean? = true, // false=must NOT exist, null=MAY exist
        value: File? = if (expectFile == false && mustExist == true) currentDirectory else null,
        var baseDirectory: File? = null,
        val baseDirectoryP: FileParameter? = null,
        val extensions: List<String>? = null)

    : AbstractValueParameter<File?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

    override val converter = object : StringConverter<File?>() {

        override fun fromString(str: String): File? {
            if (str == "") return null
            return File(str)
        }

        override fun toString(file: File?): String {
            if (file == null) {
                return ""
            }

            // Should we put a trailing "/" to indicate it is a directory?
            val suffix = if (file.isDirectory && file.path != File.separator) File.separator else ""

            baseDirectory?.let {
                file.relativeToOrNull(it)?.let {
                    return it.path + suffix
                }
            }
            return file.path + suffix
        }
    }

    init {
        if (baseDirectoryP != null) {
            baseDirectoryP.listen {
                baseDirectory = baseDirectoryP.value
            }
            baseDirectory = baseDirectoryP.value
        }
    }

    override fun errorMessage(v: File?): String? {

        val resolvedValue = resolver().resolveValue(this, v) as File? ?: v

        if (isProgrammingMode()) return null

        if (resolvedValue == null) {
            return super.errorMessage(resolvedValue)
        }

        if (mustExist != null) {
            if (resolvedValue.exists() && mustExist == false) {
                return "File already exists"
            } else if (!resolvedValue.exists() && mustExist == true) {
                return "File does not exist"
            }
        }
        if (expectFile != null) {
            if (expectFile && resolvedValue.isDirectory) {
                return "Expected a file, but is a directory"
            } else if (!expectFile && resolvedValue.isFile) {
                return "Expected a directory, but is a file"
            }
        }

        extensions?.let { list ->
            if (!list.contains(resolvedValue.extension)) {
                return "Incorrect file extension. Expected : $list"
            }
        }

        return null
    }

    fun createFileChoicesParameter(
            fileLister: FileLister,
            name: String = "file",
            hideExtension: Boolean = true)

            : ChoiceParameter<String?> {

        val filenameP = ChoiceParameter<String?>(name, value = null)
        listen {
            filenameP.clear()
            val directory = value
            if (directory?.isDirectory == true) {
                fileLister.listFiles(directory).forEach { file ->
                    filenameP.addChoice(file.name, file.name, if (hideExtension) file.nameWithoutExtension else file.name)
                }
            }
        }
        parameterListeners.fireValueChanged(this)

        return filenameP
    }

    override fun coerce(v: Any?) {
        if (v is File?) {
            value = v
            return
        }
        super.coerce(v)
    }

    override fun isStretchy(): Boolean = true

    override fun createField() = FileField(this).build() as FileField

    override fun toString() = "File" + super.toString()

    override fun copy() = FileParameter(name = name, label = label, description = description, value = value,
            required = required, mustExist = mustExist, baseDirectory = baseDirectory, expectFile = expectFile, extensions = extensions)


    override fun autoComplete(currentValue: String) {
        // System.err.println( "autoCompleting $name extensions=$extensions")
        // Leave it to the default behavior to list directory names
        if (expectFile == false) {
            //System.err.println( "System exit 123")
            System.exit(123)
        }
        // Cannot auto complete for something that doesn't exist!
        if (mustExist == false) {
            //System.err.println( "System exit 123")
            System.exit(123)
        }

        val file = File(currentValue)
        val dir: File
        if (currentValue.endsWith(File.separatorChar)) {
            dir = file
        } else {
            dir = file.parentFile ?: File(".")
        }
        val lister = FileLister(onlyFiles = true, includeHidden = true, extensions = extensions)
        val files = lister.listFiles(dir).map { if (it.path.startsWith("./")) it.path.substring(2) else it.path }
        //System.err.println("Results\n${files}\n\n")
        autoComplete(currentValue, files)
        //System.err.println( "System exit 122")
        System.exit(122)
    }

    companion object {
        var showDragIcon: Boolean = true
        var showOpenButton: Boolean = false
    }
}
