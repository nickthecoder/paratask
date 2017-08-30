/*
<PROGRAM NAME AND DESCRIPTION>
Copyright (C) <YEAR> <AUTHOR>

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

import java.io.File

/**
 * Defines the methods needed for rows that may have a file, so that all of the option filters works.
 * We need this because Groovy cannot use the Kotlin extension methods such as File.isImage(), so rather than
 * use lots of static methods, I've chosen to add these methods to the "row" objects,
 * such as WrappedFile, Place and GitStatusRow etc. All these row classes must implement this interface.
 */
interface FileTest {

    /**
     * Initially I didn't want this attribute in the interface, but without it, groovy interprets "row.file"
     * as row.isFile(), even when the row has a Kotlin "file" attribute (in the concrete class). i.e.
     * in the jvm world, there are methods "File getFile()" and "Boolean isFile()".
     */
    val file: File?

    fun isFile(): Boolean

    fun isDirectory(): Boolean

    fun isImage(): Boolean

}
