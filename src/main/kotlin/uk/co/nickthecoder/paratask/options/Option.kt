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

import uk.co.nickthecoder.paratask.Tool

interface Option {

    var code: String

    var aliases: MutableList<String>

    var label: String

    var isRow: Boolean

    var isMultiple: Boolean

    var refresh: Boolean

    var newTab: Boolean

    var prompt: Boolean

    fun run(tool: Tool, row: Any): Any?

    fun runNonRow(tool: Tool): Any?

    fun runMultiple(tool: Tool, rows: List<Any>): Any?

    fun copy(): Option
}