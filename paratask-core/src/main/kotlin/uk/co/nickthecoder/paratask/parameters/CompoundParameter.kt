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

import javafx.beans.property.SimpleStringProperty

abstract class CompoundParameter<T>(name: String, label: String, description: String = "")

    : GroupParameter(name = name, label = label, description = description),
        ValueParameter<T> {

    override fun saveChildren(): Boolean = false

    override val expressionProperty = SimpleStringProperty()

    override fun errorMessage(v: T?): String? = null
}
