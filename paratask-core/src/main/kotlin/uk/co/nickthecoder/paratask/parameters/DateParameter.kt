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
import uk.co.nickthecoder.paratask.parameters.fields.DateField
import uk.co.nickthecoder.paratask.util.uncamel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

open class DateParameter(
        name: String,
        label: String = name.uncamel(),
        description: String = "",
        hint: String = "",
        value: LocalDate? = null,
        val dateFormat: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM),
        required: Boolean = true)

    : AbstractValueParameter<LocalDate?>(
        name = name,
        label = label,
        description = description,
        hint = hint,
        value = value,
        required = required) {

    private val internalDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    override val converter = object : StringConverter<LocalDate?>() {
        override fun fromString(str: String): LocalDate? {
            if (str == "") return null
            return LocalDate.parse(str, internalDateFormat)
        }

        override fun toString(obj: LocalDate?): String = if (obj == null) "" else internalDateFormat.format(obj)
    }

    override fun errorMessage(v: LocalDate?): String? {
        if (isProgrammingMode()) return null

        if (v == null) {
            return super.errorMessage(v)
        }

        return null
    }

    var date: Date?
        get() = value?.let { return Date.from(it.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()) }
        set(v) {
            value = v?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
        }

    override fun isStretchy() = false

    override fun createField(): DateField = DateField(this).build() as DateField

    override fun toString(): String = "Date" + super.toString()

    override fun copy() = DateParameter(name = name, label = label, description = description, hint = hint,
            value = value, dateFormat = dateFormat, required = required)
}
