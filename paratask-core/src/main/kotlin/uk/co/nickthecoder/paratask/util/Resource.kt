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

import javafx.scene.image.Image
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParaTask
import java.io.File
import java.net.URL

class Resource(val url: URL) {

    val file: File? = toFile(url)

    val icon: Image? by lazy {
        val type = if (isFileOrDirectory()) {
            if (file?.isDirectory == true) "directory" else "file"
        } else {
            "web"
        }
        ParaTask.imageResource("filetypes/${type}.png")
    }

    constructor(file: File) : this(toURL(file))

    constructor(urlString: String) : this(URL(urlString))


    constructor(directoryResource: Resource, child: String) : this(URL(directoryResource.url, "${directoryResource.path}/$child"))

    val name: String
        get() {
            file?.let { return it.name }
            val str = toString()
            if (str.length > 20) {
                return "…" + str.substring(str.length - 18)
            }
            return str
        }

    val nameWithoutExtension: String
        get() {
            file?.let { return it.nameWithoutExtension }
            return name
        }

    val path: String
        get() {
            file?.let { return it.path }
            return url.toString()
        }

    // TODO Untested! Hmm, looks odd. Either fix it, delete it, or document it!
    val directoryName: String
        get() {
            file?.let { return it.name }
            val str = toString()
            file?.name ?: if (str.length > 20) {
                str.substring(18) + "…"
            }
            return str
        }

    val parentFile
        get() = file?.parentFile


    fun parentResource(): Resource {
        val path = File(url.path).parentFile
        return Resource(URL(url, path.toString() + "/"))
    }

    fun isFileOrDirectory() = file != null

    fun   isFile() = file?.isFile() == true

    fun isDirectory() = file?.isDirectory() == true

    override fun equals(other: Any?): Boolean {
        if (other is Resource) {
            return other.url == this.url
        }
        return false
    }

    override fun hashCode(): Int = url.hashCode() + 1

    override fun toString() = url.toString()


    companion object {
        fun toFile(url: URL): File? {
            try {
                return File(url.toURI())
            } catch (e: Exception) {
                return null
            }
        }

        fun toURL(file: File): URL {
            return file.toURI().toURL()
        }

        val converter = object : StringConverter<Resource?>() {
            override fun fromString(string: String?): Resource? {
                if (string == null || string == "") {
                    return null
                }
                try {
                    val firstColon = string.indexOf(':')
                    if (firstColon < 0 || firstColon == 1) {
                        // Treat as a file system path (no URL protocol)
                        val file = File(string)
                        return Resource(file)
                    }
                } catch(e: Exception) {
                }
                return Resource(URL(string))
            }

            override fun toString(obj: Resource?): String {
                if (obj == null) {
                    return ""
                }
                val file = obj.file
                file?.let {
                    return obj.file.path
                }
                return obj.url.toString()
            }
        }
    }
}
