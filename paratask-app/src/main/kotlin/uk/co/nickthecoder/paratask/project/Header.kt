/*
ParaTask Copyright (C) 2017  Nick Robinson>

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
package uk.co.nickthecoder.paratask.project

import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.parameters.Parameter

class Header(val tool: Tool, vararg rows: HeaderRow) : HeaderOrFooter(*rows) {

    constructor(tool: Tool, vararg parameters: Parameter) : this(tool, HeaderRow(*parameters))

    init {
        val lastRowIndex = rows.size - 1
        if (lastRowIndex >= 0) {
            val lastRow = rows[lastRowIndex]
            lastRow.addRunButton(tool)
        }
    }

}
