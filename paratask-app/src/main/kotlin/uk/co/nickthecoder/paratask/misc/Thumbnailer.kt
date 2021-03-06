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
package uk.co.nickthecoder.paratask.misc

import javafx.scene.image.Image
import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory
import uk.co.nickthecoder.paratask.util.isImage
import uk.co.nickthecoder.paratask.util.process.Exec
import java.io.File
import java.math.BigInteger
import java.net.URL
import java.security.MessageDigest
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Uses ImageMagick to create thumbnail images of files.
 * Note, this is NOT thread safe, so create a new Thumbnailer for each thread that needs thumbnails.
 * Typically, a Task/Tool that needs thumbnails will create its own Thumbnailer.
 * See https://specifications.freedesktop.org/thumbnail-spec/thumbnail-spec-latest.html
 *
 * Note that the height attribute is only used when creating ImageViews. The thumbnail files that are created are
 * 128x128.
 */
class Thumbnailer {

    val heightP = IntParameter("thumbnailHeight", value = DEFAULT_THUMBNAIL_SIZE)

    val directoryThumbnailP = StringParameter("directoryThumbnail", value = ".thumbnails/default.jpg")

    val messageDigest: MessageDigest = MessageDigest.getInstance("MD5")

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
            inputStream.use { inputStream ->
                return Image(inputStream)
            }
        }
        return null
    }

    fun thumbnailImageView(source: File): ImageView {

        var thumbnail: Image? = null

        if (source.isImage()) {

            thumbnail = thumbnailImage(source)

        } else if (source.isDirectory) {
            if (directoryThumbnailP.value.isNotBlank()) {
                val dirThumbnail = source.resolve(File(directoryThumbnailP.value))
                if (dirThumbnail.exists() && dirThumbnail.isFile) {
                    val i = dirThumbnail.inputStream()
                    with(i) {
                        thumbnail = Image(i)
                    }
                }
            }
        }

        if (thumbnail != null) {
            val result = ImageView()
            result.image = thumbnail
            result.fitHeight = (heightP.value ?: Thumbnailer.DEFAULT_THUMBNAIL_SIZE).toDouble()
            result.isPreserveRatio = true
            result.isSmooth = true

            return result

        } else {

            val icon = ParaTask.imageResource("filetypes/${if (source.isDirectory) "directory" else "file"}.png")
            return ImageView(icon)
        }
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
            hashText = "0" + hashText
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

    companion object {
        val DEFAULT_THUMBNAIL_SIZE = 80
    }
}
