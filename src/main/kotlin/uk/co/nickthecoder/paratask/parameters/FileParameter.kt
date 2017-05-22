package uk.co.nickthecoder.paratask.parameters

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.parameters.fields.FileField
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.homeDirectory
import uk.co.nickthecoder.paratask.util.uncamel
import java.io.File

class FileParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: File? = null,
        required: Boolean = true,
        val expectFile: Boolean? = true, // false=expect Directory, null=expect either
        val mustExist: Boolean? = true, // false=must NOT exist, null=MAY exist
        var baseDirectory: File? = null,
        val baseDirectoryP: FileParameter? = null,
        val stretchy: Boolean = true)

    : AbstractValueParameter<File?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required) {

    override val converter = object : StringConverter<File?>() {

        override fun fromString(str: String): File? {
            if (str == "") return null
            if (str == "~") return homeDirectory
            if (str.startsWith("~" + File.separatorChar)) return File(homeDirectory, str.substring(2))
            return File(str)
        }

        override fun toString(file: File?): String {
            baseDirectory?.let {
                file?.relativeToOrNull(it)?.let { return it.path }
            }
            return file?.path ?: ""
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
        if (isProgrammingMode()) return null

        if (v == null) {
            return super.errorMessage(v)
        }

        if (mustExist != null) {
            if (v.exists() && mustExist == false) {
                return "File already exists"
            } else if (!v.exists() && mustExist == true) {
                return "File does not exist"
            }
        }
        if (expectFile != null) {
            if (expectFile && v.isDirectory) {
                return "Expeceted a file, but is a directory"
            } else if (!expectFile && v.isFile) {
                return "Expeceted a directory, but is a file"
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

    override fun isStretchy(): Boolean = stretchy

    override fun createField(): FileField = FileField(this)

    override fun toString() = "File" + super.toString()

}
