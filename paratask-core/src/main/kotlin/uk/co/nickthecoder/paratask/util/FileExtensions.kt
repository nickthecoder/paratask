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

import uk.co.nickthecoder.paratask.Task
import java.io.File
import java.lang.reflect.Field

val currentDirectory: File = File("").absoluteFile

val homeDirectory: File = File(System.getProperty("user.home"))

val applicationDirectory: File
    get() {
        val codeLocation = Task::class.java.protectionDomain.codeSource.location
        if (codeLocation.path.endsWith(".jar")) {
            return File(codeLocation.toURI().path).parentFile.parentFile
        }
        // Running in development environment, such as an Eclipse/IntelliJ, without using jar files
        return currentDirectory.child("src", "dist")
    }

val imageExtensions = hashSetOf("bmp", "cmyk", "cmyka", "dpf", "eps", "gif", "ico", "jpeg", "jpg", "mng", "pbm", "pcx", "png", "pnm", "ps", "psd", "raw", "rgb", "rgba", "svg", "tga", "tif", "tiff", "webp", "xcf", "xpm")

val videoExtensions = hashSetOf("webm", "mkv", "flv", "vob", "ogv", "ogg", "drc", "avi", "mov", "qt", "wmv", "rm", "mp4", "m4v", "mpg", "mpg2", "mpeg", "mpv", "mpe", "m2v")

fun File.child(vararg names: String): File {
    var f = this
    for (name in names) {
        f = File(f, name)
    }
    return f
}

fun File.isImage() = this.extension.toLowerCase() in imageExtensions

fun File.isVideo() = this.extension.toLowerCase() in videoExtensions

/**
 * Java uses a cache to speed up File.canonicalFile, and I need to bypass this cache.
 * There is a field on FileSystem, but it is not public, so I need to jump through hoops to change it, and
 * then change it back. Also, as this is non-public, there is no guarantee that future versions of java will
 * use this field. Therefore we must be careful to try our best and handle exceptions gracefully.
 * Therefore, I get the value, attempt to change it, and then restore the value. If any of these step fail, then
 * catch and ignore the exceptions. (In which case the result of this method may still return stale data).
 */
fun File.getUncachedCanonicalFile(): File? {

    var useCanonCachesOldValue: Any? = null
    var field: Field? = null

    try {
        val klass = Class.forName("java.io.FileSystem")
        field = klass.getDeclaredField("useCanonCaches")
        field.isAccessible = true
        useCanonCachesOldValue = field.get(null)
        field.set(null, false)
    } catch (e: Exception) {
        // Ignore - we did our best!
    }

    try {
        return this.canonicalFile

    } finally {
        try {
            if (useCanonCachesOldValue != null) {
                field?.set(null, useCanonCachesOldValue)
            }
        } catch (e: Exception) {
            // Ignore - we did our best!
        }
    }
}
