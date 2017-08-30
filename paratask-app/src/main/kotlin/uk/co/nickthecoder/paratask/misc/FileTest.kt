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
package uk.co.nickthecoder.paratask.misc

import uk.co.nickthecoder.paratask.util.isImage
import uk.co.nickthecoder.paratask.util.isVideo
import java.io.File

/**
 * Defines the methods needed for rows that may have a file, so that all of the option filters works.
 * We need this because Groovy cannot use the Kotlin extension methods such as File.isImage(), so rather than
 * use lots of static methods, I've chosen to add these methods to the "row" objects,
 * such as WrappedFile, Place and GitStatusRow etc. All these row classes must implement this interface.
 */
interface FileTest {

    val file: File?

    fun isFile(): Boolean = file?.isFile == true

    fun isDirectory(): Boolean = file?.isDirectory == true

    fun isImage(): Boolean = file?.isImage() == true

    fun isVideo(): Boolean = file?.isVideo() == true

}
