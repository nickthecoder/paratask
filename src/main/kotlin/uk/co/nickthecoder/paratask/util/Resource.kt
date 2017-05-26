package uk.co.nickthecoder.paratask.util

import javafx.scene.image.Image
import javafx.util.StringConverter
import uk.co.nickthecoder.paratask.ParaTaskApp
import java.io.File
import java.net.URL

class Resource(val url: URL) {

    val file: File? = toFile(url)

    val icon: Image? by lazy {
        val type = if (isFile()) {
            if (file?.isDirectory == true) "directory" else "file"
        } else {
            "web"
        }
        ParaTaskApp.imageResource("filetypes/${type}.png")
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

    val directoryName: String
        get() {
            file?.let { return it.name }
            val str = toString()
            file?.name ?: if (str.length > 20) {
                str.substring(18) + "…"
            }
            return str
        }

    fun parentResource(): Resource {
        return Resource(this, ".")
    }

    fun isFile() = file != null

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
