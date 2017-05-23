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
import java.lang.ref.WeakReference
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import java.util.*

class FileWatcher {

    private val watchServiceByFileSystem = mutableMapOf<FileSystem, WatchService>()

    private val entriesByDirectory: MutableMap<Path, MutableList<Entry>> = mutableMapOf<Path, MutableList<Entry>>()

    fun register(file: File, listener: FileListener, useCanonical : Boolean = true) {
        register( (if (useCanonical) file.canonicalFile else file).toPath(), listener)
    }

    fun register(file: Path, listener: FileListener) {

        val directory: Path
        if (Files.isDirectory(file)) {
            directory = file
        } else {
            directory = file.parent
        }

        var list: MutableList<Entry>? = entriesByDirectory[directory]
        if (list == null) {
            list = ArrayList<Entry>()
            entriesByDirectory.put(directory, list)
            directory.register(watchService(directory), ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)
        }
        list.add(Entry(listener, file))
    }

    fun unregister(listener: FileListener) {
        for (list in entriesByDirectory.values) {
            for (entry in list) {
                if (entry.weakListener.get() === listener) {
                    list.remove(entry)
                    return
                }
            }
        }
    }

    fun unregister(file: Path, dl: FileListener) {
        val directory: Path
        if (Files.isDirectory(file)) {
            directory = file
        } else {
            directory = file.parent
        }

        val list: MutableList<Entry> = entriesByDirectory[directory] ?: return

        for (entry in list) {
            if (entry.weakListener.get() === dl) {
                list.remove(entry)
                break
            }
        }
        if (list.isEmpty()) {
            entriesByDirectory.remove(directory)
        }
    }

    private fun watchService(directory: Path): WatchService = watchService(directory.fileSystem)

    private fun watchService(filesystem: FileSystem): WatchService =
            watchServiceByFileSystem[filesystem] ?: createWatchService(filesystem)

    private fun createWatchService(filesystem: FileSystem): WatchService {
        val watchService = filesystem.newWatchService()!!
        watchServiceByFileSystem.put(filesystem, watchService)
        startPolling(watchService)
        return watchService
    }

    private fun startPolling(watcher: WatchService) {
        val thread = Thread(Runnable {
            while (true) {
                poll(watcher)
            }
        })
        thread.isDaemon = true
        thread.start()
    }

    private fun poll(watcher: WatchService) {
        val key: WatchKey
        try {
            key = watcher.take()
        } catch (x: InterruptedException) {
            return
        }

        val directory = key.watchable() as Path

        /*
         * I used this tutorial, but it seems broken, as it misses some changes :-(
         * https://docs.oracle.com/javase/tutorial/essential/io/notification.html
         *
         * We cannot reply on WatchService to tell us about ALL changes to the directory, because
         * we need to perform a reset in order to receive further changes. If the directory is changed AGAIN
         * before the reset is called, then we will not see the 2nd change. This is common, because
         * when saving documents, an application will often save to a temporary file, and then rename it to
         * the real file (e.g. gedit does this).
         * So instead, we keep track of each listener's file's last modified time-stamp, and check each listener of
         * this directory to see if their file has changed.
         *
         * i.e. we ignore the result of key.pollEvents()
         */
        key.pollEvents()

        if (!notifyListeners(directory)) {
            key.cancel()
            return
        }

        val valid = key.reset()
        if (!valid) {
            // Directory has been deleted, so remove all the listeners
            entriesByDirectory.remove(directory)
        }
    }

    private fun notifyListeners(directory: Path): Boolean {
        val list = entriesByDirectory[directory] ?: return false
        if (list.isEmpty()) {
            entriesByDirectory.remove(directory)
            return false
        }

        for (entry in list) {
            val listener = entry.weakListener.get()
            if (listener == null) {
                list.remove(entry)
                // Recurse to prevent concurrent modification exception.
                return notifyListeners(directory)
            }
            try {
                entry.check()
            } catch (e: Exception) {
                e.printStackTrace()
                list.remove(entry)
                // Recurse to prevent concurrent modification exception.
                return notifyListeners(directory)
            }

        }
        return true
    }

    private inner class Entry(listener: FileListener, private val path: Path) {

        internal val weakListener = WeakReference<FileListener>(listener)

        internal var lastModified: Long = path.toFile().lastModified()

        fun check() {
            val lm = path.toFile().lastModified()
            if (lastModified != lm) {
                val listener = this.weakListener.get()
                if (listener != null) {
                    this.lastModified = lm
                    listener!!.fileChanged(path)
                }
            }
        }

        override fun toString(): String {
            return "FileWatching Entry for listener ${weakListener.get()}"
        }
    }

    companion object {
        var instance: FileWatcher = FileWatcher()
    }
}
