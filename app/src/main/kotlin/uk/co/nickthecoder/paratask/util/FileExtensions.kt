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

package uk.co.nickthecoder.paratask.util

import java.io.File

val currentDirectory: File = File("").absoluteFile

val homeDirectory: File = File(System.getProperty("user.home"))

val imageExtensions = hashSetOf("bmp", "cmyk", "cmyka", "dpf", "eps", "gif", "ico", "jpeg", "jpg", "mng", "pbm", "pcx", "png", "pnm", "ps", "psd", "raw", "rgb", "rgba", "svg", "tga", "tif", "tiff", "webp", "xcf", "xpm")

fun File.child(vararg names: String): File {
    var f = this
    for (name in names) {
        f = File(f, name)
    }
    return f
}

fun File.isImage() = this.extension.toLowerCase() in imageExtensions
