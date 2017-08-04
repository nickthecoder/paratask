package uk.co.nickthecoder.paratask.misc

import javafx.scene.image.Image
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory
import uk.co.nickthecoder.paratask.util.process.Exec
import java.io.File
import java.math.BigInteger
import java.net.URL
import java.security.MessageDigest
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Uses ImageMagick to create thumbnail images of files.
 * Note, this is NOT thread safe, so create a new Thumbnailer for each thread that needs thumbnails.
 * Tyically, a Task/Tool that needs thumbnails will create its own Thumbnailer.
 * See https://specifications.freedesktop.org/thumbnail-spec/thumbnail-spec-latest.html
 */
class Thumbnailer {

    val messageDigest = MessageDigest.getInstance("MD5")

    private val queue = ConcurrentLinkedQueue<File>()

    private var thread: Thread? = null

    // According to the spec, this is the WRONG directory, it should be ~/.cache/thumbnails/normal
    // But I will follow KDE, rather than the spec ;-(
    val thumbnailDirectory = homeDirectory.child(".thumbnails", "normal")

    fun thumbnailImage(source: File): Image? {
        val thumbFile = thumbnailFile(source)

        if (!thumbFile.exists() || thumbFile.lastModified() < source.lastModified()) {
            queueThumbnail(source)
        }

        if (thumbFile.exists()) {
            val inputStream = thumbFile.inputStream()
            try {
                return Image(inputStream)
            } finally {
                inputStream.close()
            }
        }
        return null
    }

    fun thumbnailFile(source: File): File {
        // According to the spec, the canonical filename should be used, however, KDE seem to ignore this
        // So, I follow KDE's lead IF the file exists, but if it doesn't then I fall back to the
        // "correct" solution, and use the canonical filename.
        val wrongFile = thumbnailFile(source.toURI().toURL())
        if (wrongFile.exists()) {
            return wrongFile
        }

        try {
            return thumbnailFile(source.canonicalFile.toURI().toURL())
        } catch (e: Exception) {
        }
        return wrongFile
    }

    fun thumbnailFile(url: URL): File {
        var urlString = url.toString()
        if (urlString.startsWith("file:/") && !urlString.startsWith("file:///")) {
            urlString = "file:///" + urlString.substring(6)
        }
        return File(thumbnailDirectory, md5(urlString) + ".png")
    }

    fun md5(string: String): String {
        messageDigest.reset()
        messageDigest.update(string.toByteArray())
        val digest = messageDigest.digest()
        val bigInt = BigInteger(1, digest)
        var hashText = bigInt.toString(16)

        // Zero pad to 32 character
        while (hashText.length < 32) {
            hashText = "0" + hashText;
        }
        return hashText
    }

    fun queueThumbnail(source: File) {
        queue.add(source)

        if (thread == null) {
            thread = object : Thread() {
                override fun run() {
                    processThumbnailQueue()
                }
            }
            thread?.start()
        }
    }

    private fun processThumbnailQueue() {
        var source = queue.poll()
        while (source != null) {

            createThumbnail(source, thumbnailFile(source))

            source = queue.poll()
        }

        thread = null
    }

    fun createThumbnail(source: File, thumbFile: File) {
        if (thumbFile.exists() && thumbFile.lastModified() >= source.lastModified()) {
            return
        }

        if (!thumbnailDirectory.exists()) {
            thumbnailDirectory.mkdirs()
        }

        val convert = Exec("convert", source, "-format", "png", "-thumbnail", "128x128", thumbFile)
        convert.start()
        convert.waitFor()
        val chmod = Exec("chmod", "600", thumbFile)
        chmod.start()
    }
}
