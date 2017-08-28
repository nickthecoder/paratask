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
package uk.co.nickthecoder.paratask.tools.places

import javafx.collections.ObservableList
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.paratask.gui.ApplicationActions
import uk.co.nickthecoder.paratask.gui.DragFilesHelper
import uk.co.nickthecoder.paratask.util.FileLister
import java.io.File

class DirectoryTree(
        rootDirectory: File = File.listRoots()[0],
        val includeHidden: Boolean = false,
        foldSingleDirectories: Boolean = true)

    : TreeView<String>() {

    var rootDirectory: File = rootDirectory
        set(v) {
            if (v != field) {
                val oldRoot = root
                root = DirectoryItem(v)
                reExpand(oldRoot as DirectoryItem)
            }
        }

    var foldSingleDirectories: Boolean = foldSingleDirectories
        set(v) {
            field = v
            rebuild()
        }

    var onSelected: ((File) -> Unit)? = null

    val dragHelper = DragFilesHelper() {
        val item = selectionModel.selectedItemProperty().get() as DirectoryItem?
        if (item == null) {
            null
        } else {
            listOf(item.directory)
        }
    }

    val selectedDirectory: File?
        get() = (selectionModel.selectedItem as DirectoryItem?)?.directory


    init {
        isEditable = false
        root = DirectoryItem(rootDirectory)
        root.children
        // Prevent Double Click expanding/contracting the item (as this is used to show the contents of the directory).
        addEventFilter(MouseEvent.MOUSE_PRESSED) { if (it.clickCount == 2) it.consume() }
        addEventFilter(MouseEvent.MOUSE_CLICKED) { onMouseClicked(it) }
        addEventFilter(KeyEvent.KEY_PRESSED) { onKeyPressed(it) }

        root.isExpanded = true
        dragHelper.applyTo(this)
    }

    fun onMouseClicked(event: MouseEvent) {
        if (event.clickCount == 2) {
            onSelect()
            event.consume()
        }
    }

    fun onKeyPressed(event: KeyEvent) {
        if (ApplicationActions.ENTER.match(event)) {
            onSelect()
            event.consume()
        } else if (ApplicationActions.SPACE.match(event)) {
            selectionModel.selectedItem?.let {
                it.isExpanded = !it.isExpanded
                event.consume()
            }
        }
    }

    fun onSelect() {
        onSelected?.let { handler ->
            selectedDirectory?.let {
                handler(it)
            }
        }
    }

    /**
     * Finds the item in the tree for a given directory, or null if it is not in the tree.
     */
    fun findItem(directory: File): DirectoryItem? {
        val rootItem = root as DirectoryItem

        if (rootItem.directory == directory) {
            return rootItem

        } else {
            // List all of the parent directories up to the tree's root.
            val parents = mutableListOf<File>()
            var d1: File? = directory
            while (d1 != null) {
                if (d1 == rootDirectory) {
                    break
                }
                parents.add(0, d1)
                d1 = d1.parentFile
            }

            // Start at the root node, and look for child nodes that are in the list
            var item: DirectoryItem? = rootItem
            while (item != null) {
                val found = item.children.filterIsInstance<DirectoryItem>().filter { parents.contains(it.directory) }.firstOrNull()
                if (found == null) {
                    return item
                }
                item = found
            }
            return null
        }
    }

    /**
     * If directory is somewhere below rootDirectory, then expand the items as needed and select the item
     * corresponding to the directory given.
     */
    fun selectDirectory(directory: File): DirectoryItem? {
        val item = findItem(directory)
        item?.let { selectionModel.select(item) }
        return item
    }


    private fun reExpand(oldItem: DirectoryItem) {

        if (oldItem.isExpanded) {
            val foundItem = findItem(oldItem.directory)
            foundItem?.isExpanded = true
            oldItem.children.filterIsInstance<DirectoryItem>().forEach {

                if (!foldSingleDirectories) {
                    // Exapnd all previously folded items
                    var parent: File? = oldItem.directory.parentFile
                    while (parent != oldItem.directory && parent != null) {
                        findItem(parent)?.isExpanded = true
                        parent = parent.parentFile
                    }
                }

                reExpand(it)
            }
        }
    }

    fun rebuild() {
        val oldRoot = root as DirectoryItem
        root = DirectoryItem(rootDirectory)
        reExpand(oldRoot)
    }

    inner class DirectoryItem(val directory: File, label: String = directory.name) : TreeItem<String>(label) {

        var firstTimeChildren = true

        override fun getChildren(): ObservableList<TreeItem<String>> {
            val superChildren = super.getChildren()
            if (firstTimeChildren) {
                firstTimeChildren = false
                val lister = FileLister(onlyFiles = false, includeHidden = includeHidden)
                lister.listFiles(directory).forEach { child ->
                    superChildren.add(createChild(child))
                }
            }

            return superChildren
        }

        private fun createChild(subDir: File, prefix: String = ""): DirectoryItem {
            if (foldSingleDirectories) {
                val lister = FileLister(onlyFiles = null, includeHidden = includeHidden)

                val grandChildren = lister.listFiles(subDir)
                if (grandChildren.size == 1 && grandChildren[0].isDirectory) {
                    val newPrefix = prefix + subDir.name + File.separator
                    return createChild(grandChildren[0], prefix = newPrefix)
                }
            }
            return DirectoryItem(subDir, prefix + subDir.name)
        }

        fun isFolded(): Boolean {
            return (parent as DirectoryItem).directory != directory.parentFile
        }

        override fun isLeaf(): Boolean {
            return getChildren().isEmpty()
        }

        override fun toString(): String = directory.name

    }
}
