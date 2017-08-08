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

package uk.co.nickthecoder.paratask.options

import javafx.scene.input.KeyCodeCombination
import java.io.Serializable

abstract class AbstractOption(
        override var code: String = "",
        override var aliases: MutableList<String> = mutableListOf<String>(),
        override var label: String = "",
        override var isRow: Boolean = true,
        override var isMultiple: Boolean = false,
        override var newTab: Boolean = false,
        override var prompt: Boolean = false,
        override var refresh: Boolean = false,
        override var shortcut: KeyCodeCombination? = null
) : Option, Serializable {

    protected fun copyTo(result: AbstractOption) {
        result.code = code
        result.aliases = ArrayList<String>(aliases)
        result.label = label
        result.isRow = isRow
        result.isMultiple = isMultiple
        result.newTab = newTab
        result.prompt = prompt
        result.refresh = refresh
        result.shortcut = shortcut
    }
}
