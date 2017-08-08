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

package uk.co.nickthecoder.paratask.parameters.fields

import javafx.application.Platform
import javafx.scene.control.DatePicker
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.parameters.DateParameter
import uk.co.nickthecoder.paratask.parameters.ParameterEvent
import java.time.LocalDate


class DateField(val dateParameter: DateParameter) : LabelledField(dateParameter) {

    val datePicker = DatePicker()

    val converter = object : StringConverter<LocalDate?>() {
        override fun fromString(str: String): LocalDate? {
            if (str == "") return null
            return LocalDate.parse(str, dateParameter.dateFormat)
        }

        override fun toString(obj: LocalDate?): String = if (obj == null) "" else dateParameter.dateFormat.format(obj)
    }

    /**
     * By default, when you type an invalid date, then the date picker loses focus, the text is replaced
     * with the date picker's old internal value, i.e. reverting the changes you have made.
     * My code attempts to "fix"/"bodge" this crappy behaviour.
     *
     * Code related to this comment is maked with "Bodge SEE ABOVE"
     */
    private var editorText: String = ""

    override fun createControl(): DatePicker {

        datePicker.converter = converter
        datePicker.valueProperty().bindBidirectional(dateParameter.valueProperty)

        // Bodge. SEE ABOVE
        editorText = datePicker.editor.text
        // End bodge

        datePicker.editor.addEventHandler(KeyEvent.KEY_PRESSED, { event ->

            if (acceleratorEnter.match(event)) {
                processEnter()
                event.consume()
            }

            // Bodge SEE ABOVE
            if (event.code != KeyCode.TAB) {
                Platform.runLater {
                    editorText = datePicker.editor.text
                }
            }
            // end bodge

        })

        // Bodge SEE ABOVE
        datePicker.focusedProperty().addListener { _, _, newValue ->
            if (newValue == false) {
                Platform.runLater {
                    datePicker.editor.text = editorText
                }
            }
        }
        // end bodge

        return datePicker
    }

    override fun isDirty(): Boolean {
        try {
            val v = datePicker.converter.fromString(editorText)
            showOrClearError(dateParameter.errorMessage(v))
            return false
        } catch (e: Exception) {
            showError("Not a valid date")
            return true
        }
    }

    /**
     * DatePicker normally consume the ENTER key, which means the default button won't be run when ENTER is
     * pressed. My control doesn't need to handle the ENTER key, and therefore, this code
     * re-introduces the expected behaviour of the ENTER key (i.e. performing the default button's action).
     */
    private fun processEnter() {
        val defaultRunnable = scene?.accelerators?.get(acceleratorEnter)
        defaultRunnable?.let { defaultRunnable.run() }
    }

    override fun parameterChanged(event: ParameterEvent) {
        super.parameterChanged(event)

        editorText = converter.toString(dateParameter.value)
    }
}
