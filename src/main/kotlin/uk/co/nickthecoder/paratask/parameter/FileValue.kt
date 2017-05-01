package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleObjectProperty
import javafx.util.StringConverter
import java.io.File

class FileValue(
        override val parameter: FileParameter,
        initialValue: File? = null)

    : AbstractValue<File?>(initialValue) {

    override fun fromString(str: String): File? = if (str == "") null else File(str)

    override fun toString(obj: File?): String = value?.toString() ?: ""

    override fun errorMessage(v: File?): String? {
        return parameter.errorMessage(v)
    }

    override fun toString(): String = "File" + super.toString()

}