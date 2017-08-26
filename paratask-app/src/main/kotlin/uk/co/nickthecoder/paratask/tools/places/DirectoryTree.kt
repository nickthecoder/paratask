package uk.co.nickthecoder.paratask.tools.places

import javafx.collections.ObservableList
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import uk.co.nickthecoder.paratask.gui.ApplicationActions
import uk.co.nickthecoder.paratask.gui.DragFilesHelper
import uk.co.nickthecoder.paratask.project.ParataskActions
import uk.co.nickthecoder.paratask.util.FileLister
import java.io.File

class DirectoryTree(rootDirectory: File = File.listRoots()[0], val includeHidden: Boolean = false)
    : TreeView<String>() {

    var rootDirectory: File = rootDirectory
        set(v) {
            root = DirectoryItem(v)
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
        root = DirectoryItem(rootDirectory)
        root.children
        addEventFilter(MouseEvent.MOUSE_CLICKED) { onMouseClicked(it) }
        addEventHandler(KeyEvent.KEY_PRESSED) { onKeyPressed(it) }

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
        } else if (ApplicationActions.SPACE.match(event)) {
            selectionModel.selectedItem?.let {
                it.isExpanded = !it.isExpanded
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
            val parents = mutableListOf<File>()
            var d1: File? = directory
            while (d1 != null) {
                if (d1 == rootDirectory) {
                    break
                }
                parents.add(0, d1)
                d1 = d1.parentFile
            }
            var item: DirectoryItem? = rootItem
            parents.forEach { d2 ->
                item = item?.children?.filterIsInstance<DirectoryItem>()?.firstOrNull {
                    it.directory == d2
                }
            }
            return item
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

    inner class DirectoryItem(val directory: File) : TreeItem<String>(directory.name) {

        var firstTimeChildren = true

        override fun getChildren(): ObservableList<TreeItem<String>> {
            val superChildren = super.getChildren()
            if (firstTimeChildren) {
                firstTimeChildren = false
                val lister = FileLister(onlyFiles = false, includeHidden = includeHidden)
                lister.listFiles(directory).forEach {
                    superChildren.add(DirectoryItem(it))
                }
            }
            return superChildren
        }

        override fun isLeaf(): Boolean {
            return getChildren().isEmpty()
        }

        override fun toString(): String = directory.name
    }
}
