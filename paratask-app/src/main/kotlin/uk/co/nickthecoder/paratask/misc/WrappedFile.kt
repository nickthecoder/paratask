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

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.util.isImage
import java.io.File

open class WrappedFile(override val file: File) : FileTest {

    val icon by lazy {
        ParaTask.imageResource("filetypes/${if (file.isDirectory) "directory" else "file"}.png")
    }

    // Not only a convenience method, but also so that options can be used interchangably between WrappedFile and
    // Place (and any future rows that *may* contain File objects).
    override fun isFile() = file.isFile

    // Not only a convenience method, but also so that options can be used interchangably between WrappedFile and
    // Place (and any future rows that *may* contain File objects).
    override fun isDirectory() = file.isDirectory

    override fun isImage() = file.isImage()

    override fun toString() = "WrappedFile( $file )"
}
