package uk.co.nickthecoder.paratask.parameter

import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.gui.field.FileField
import uk.co.nickthecoder.paratask.util.homeDirectory
import uk.co.nickthecoder.paratask.util.uncamel
import java.io.File

class FileParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        value: File? = homeDirectory,
        required: Boolean = true,
        columns: Int = 30,
        val stretchy: Boolean = true)

    : TextParameter<File?>(
        name = name,
        label = label,
        description = description,
        value = value,
        required = required,
        columns = columns) {

    override val converter = object : StringConverter<File?>() {

        override fun fromString(str: String): File? = if (str == "") null else File(str)

        override fun toString(file: File?): String = file?.getPath() ?: ""
    }

    override fun isStretchy(): Boolean = stretchy

    override fun createField(): FileField = FileField(this)

    override fun toString() = "File" + super.toString()

}