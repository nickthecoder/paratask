package uk.co.nickthecoder.paratask.parameter

import javafx.beans.property.SimpleObjectProperty
import javafx.util.StringConverter
import java.io.File

class FileValue(override val parameter: FileParameter, initialValue: File? = null)
    : StringConverter<File?>(), Value<File?> {

    override val valueListeners = ValueListeners()

    var stringProperty = object : SimpleObjectProperty<String>() {
        override fun set(v: String) {
            if (v != get()) {
                super.set(v)
                stringValue = v
            }
        }
    }

    override var value: File? = initialValue
        set(v: File?) {
            val changed = v != field
            if (changed) {
                field = v
                stringProperty.set(stringValue)
                valueListeners.fireChanged(this)
            }
        }

    init {
        stringProperty.set(stringValue)
    }

    override var stringValue: String
        get() = toString(value)
        set(v: String) {
            value = fromString(v)
        }

    override fun fromString(str: String): File? = if (str == "") null else File(str)

    override fun toString(obj: File?): String = value?.toString() ?: ""

    override fun errorMessage() = errorMessage(value)

    fun errorMessage(v: File?) = parameter.errorMessage(v)

    override fun toString(): String = "StringValue name '${parameter.name}' = '${value}'"

}