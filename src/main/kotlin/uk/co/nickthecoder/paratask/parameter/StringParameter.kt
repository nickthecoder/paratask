package uk.co.nickthecoder.paratask.parameter

import javafx.scene.control.TextField

class StringParameter(name: String, required: Boolean = true, columns: Int = 30, val stretchy: Boolean = true)
    : TextParameter<String>(name = name, required = required, columns = columns) {
    override fun setStringValue(s: String) {
        value = s
    }

    override fun isStretchy(): Boolean = stretchy

    override fun bind(textField: TextField) {
        textField.textProperty().bindBidirectional(property)
    }

    override fun errorMessage(v: String?): String? {
        if (v == null) {
            return "Null values not allowed"
        }
        if (v.length == 0) {
            return super.errorMessage(null)
        }
        return null;
    }
}