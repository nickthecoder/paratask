package uk.co.nickthecoder.paratask.util

import javafx.util.StringConverter
import java.io.File
import java.net.URL

class Resource(val url: URL) {

    val file: File? = toFile(url)

    constructor(file: File) : this(toURL(file))

    fun isFile() = file != null

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
