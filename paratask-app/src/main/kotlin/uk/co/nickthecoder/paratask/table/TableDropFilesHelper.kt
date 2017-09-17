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
package uk.co.nickthecoder.paratask.table

import javafx.scene.input.DataFormat
import javafx.scene.input.TransferMode
import java.io.File

/**
 * A helper class for table tools which can be a drop target for files.
 * When dropping onto the table, you can distinguish between dropping to the table as a whole,
 * or dropping to a single row of the table.
 * R is the type or Row
 */
abstract class TableDropFilesHelper<R : Any>(
        modes: Array<TransferMode> = TransferMode.ANY)

    : TableDropHelper<List<File>, R>(
        dataFormat = DataFormat.FILES,
        modes = modes)
