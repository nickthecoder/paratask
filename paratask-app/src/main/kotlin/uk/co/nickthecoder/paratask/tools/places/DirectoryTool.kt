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

package uk.co.nickthecoder.paratask.tools.places

import javafx.application.Platform
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.TabPane
import javafx.scene.control.ToolBar
import javafx.scene.image.ImageView
import javafx.scene.input.TransferMode
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.SidePanel
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.gui.*
import uk.co.nickthecoder.paratask.misc.AutoRefresh
import uk.co.nickthecoder.paratask.misc.FileOperations
import uk.co.nickthecoder.paratask.misc.Thumbnailer
import uk.co.nickthecoder.paratask.misc.WrappedFile
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.project.*
import uk.co.nickthecoder.paratask.table.*
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import uk.co.nickthecoder.paratask.table.filter.SingleRowFilter
import uk.co.nickthecoder.paratask.util.FileLister
import uk.co.nickthecoder.paratask.util.HasDirectory
import uk.co.nickthecoder.paratask.util.currentDirectory
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.File

class DirectoryTool : AbstractTableTool<WrappedFile>(), HasDirectory, SingleRowFilter<WrappedFile> {

    override val taskD = TaskDescription(name = "directory", description = "Work with a Single Directory")

    val directoriesP = MultipleParameter("directories", value = listOf(currentDirectory), minItems = 1) {
        FileParameter("dir", label = "Directory", expectFile = false, mustExist = true)
    }

    val treeRootP = FileParameter("treeRoot", required = false, expectFile = false)

    val placesFileP = FileParameter("placesFile", required = false, expectFile = true)

    val filterGroupP = GroupParameter("filter")

    val onlyFilesP = BooleanParameter("onlyFiles", required = false, value = null)
    val extensionsP = MultipleParameter("extensions", label = "File Extensions") { StringParameter("") }
    val includeHiddenP = BooleanParameter("includeHidden", value = false)

    val foldSingleDirectoriesP = BooleanParameter("foldSingleDirectories", value = true)

    val autoRefreshP = uk.co.nickthecoder.paratask.parameters.BooleanParameter("autoRefresh", value = true,
            description = "Refresh the list when the contents of the directory changes")


    val autoRefresh = AutoRefresh { toolPane?.parametersPane?.run() }

    val thumbnailer = Thumbnailer()

    val treeRoot: File
        get() = treeRootP.value ?: homeDirectory

    override val hasSidePanel = true

    override val directory: File?
        get() {
            return selectedDirectoryTableResults()?.directory ?: directoriesP.value.firstOrNull()
        }


    /**
     * The results Map of directory to list of files listed for the directory.
     */
    var lists = mutableMapOf<File, List<WrappedFile>>()

    // Used to select the correct ResultsTab when refreshing the tool
    var latestDirectory: File? = null

    // When a directory is changed, we use this, rather than latestDirectory to choose which ResultsTab to select
    var selectDirectory: File? = null

    override val rowFilter = RowFilter<WrappedFile>(this, columns, WrappedFile(File("")))


    init {

        filterGroupP.addParameters(onlyFilesP, extensionsP, includeHiddenP)
        taskD.addParameters(
                directoriesP, treeRootP, placesFileP, filterGroupP, foldSingleDirectoriesP,
                thumbnailer.heightP, thumbnailer.directoryThumbnailP, autoRefreshP)
        taskD.unnamedParameter = directoriesP

        columns.add(Column<WrappedFile, ImageView>("icon", label = "", getter = { thumbnailer.thumbnailImageView(it.file) }))
        columns.add(FileNameColumn<WrappedFile>("name", getter = { it.file }))
        columns.add(TimestampColumn<WrappedFile>("modified", getter = { it.file.lastModified() }))
        columns.add(SizeColumn<WrappedFile>("size", getter = { it.file.length() }))
    }

    override fun loadProblem(parameterName: String, expression: String?, stringValue: String?) {
        if (parameterName == "directory") {
            directoriesP.clear()
            if (expression != null) {
                val inner = directoriesP.newValue()
                inner.expression = expression
            } else {
                directoriesP.addValue(File(stringValue))
            }
        } else if (parameterName == "depth" || (parameterName == "includeBase") || (parameterName == "enterHidden")) {
            // Do nothing - we no longer use these parameters
            // They were for the rather naff "DirectoryTreeTool", which no longer exists.
        } else {
            super<AbstractTableTool>.loadProblem(parameterName, expression, stringValue)
        }
    }

    override fun run() {
        latestDirectory = selectDirectory ?: selectedDirectoryTableResults()?.directory
        selectDirectory = null
        Platform.runLater {
            directorySidePanel?.update()
        }

        directoriesP.value.filterNotNull().forEach { dir ->
            listDirectory(dir)
        }
        updateTitle()

        autoRefresh.unwatchAll()
        if (autoRefreshP.value == true) {
            directoriesP.value.filterNotNull().forEach {
                autoRefresh.watch(it)
            }
        }

    }

    var directorySidePanel: DirectorySidePanel? = null

    override fun getSidePanel(): DirectorySidePanel {
        if (directorySidePanel == null) {
            directorySidePanel = DirectorySidePanel()
        }
        return directorySidePanel!!
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)

        val button = toolPane.tabPane.createAddTabButton {
            onAddDirectory()
        }
        val dropHelper = DropFiles(arrayOf(TransferMode.COPY)) { _, files ->
            files.filter { it.isDirectory }.forEach {
                addDirectory(it)
            }
        }
        dropHelper.applyTo(button)
    }

    override fun detaching() {
        super.detaching()
        autoRefresh.unwatchAll()
    }

    fun createHeaderRows(dirP: FileParameter): Header = Header(this, dirP)

    fun createResults(dirP: FileParameter): Results {
        val dir = dirP.value!!
        val list = lists[dir]!!
        val tableResults = DirectoryTableResults(dir, list)

        return ResultsWithHeader(tableResults, createHeaderRows(dirP))
    }

    override fun createResults(): List<Results> {
        return directoriesP.innerParameters.filter { it.value != null }.map { dirP ->
            createResults(dirP as FileParameter)
        }
    }

    fun updateTitle() {
        shortTitle = directory?.name ?: "Directory"
        longTitle = "Directory ${directory?.path}"
    }

    fun listDirectory(directory: File) {
        val lister = FileLister(
                onlyFiles = onlyFilesP.value,
                includeHidden = includeHiddenP.value!!,
                extensions = extensionsP.value
        )

        lists[directory] = lister.listFiles(directory).map { WrappedFile(it) }

    }

    fun selectedDirectoryTableResults(): DirectoryTableResults? {
        val res = toolPane?.currentResults()
        if (res is ResultsWithHeader) {
            val inner = res.results
            if (inner is DirectoryTableResults) {
                return inner
            }
        }
        return null
    }

    fun onAddDirectory() {
        val newFileP = directoriesP.addValue(null)

        focusOnParameter(newFileP)
    }

    fun addDirectory(directory: File) {
        directoriesP.addValue(directory) as FileParameter
        selectDirectory = directory
        toolPane?.parametersPane?.run()
    }

    fun changeDirectory(directory: File) {
        val oldDirectory = selectedDirectoryTableResults()?.directory
        if (oldDirectory != directory) {
            directoriesP.replace(oldDirectory, directory)
            selectDirectory = directory
            toolPane?.parametersPane?.run()
        }
    }


    inner class DirectoryDropHelper(val directory: File) : TableDropFilesHelper<WrappedFile>() {

        override fun acceptDropOnRow(row: WrappedFile): Array<TransferMode>? = if (row.isDirectory()) TransferMode.ANY else null

        override fun acceptDropOnNonRow(): Array<TransferMode> = TransferMode.ANY

        override fun droppedOnRow(row: WrappedFile, content: List<File>, transferMode: TransferMode) {
            if (row.isDirectory()) {
                FileOperations.instance.fileOperation(content, row.file, transferMode)
            }
        }

        override fun droppedOnNonRow(content: List<File>, transferMode: TransferMode) {
            FileOperations.instance.fileOperation(content, directory, transferMode)
        }
    }


    inner class DirectoryTableResults(val directory: File, list: List<WrappedFile>)
        : TableResults<WrappedFile>(this@DirectoryTool, list, directory.name, columns, rowFilter = rowFilter, canClose = true) {

        init {
            dropHelper = DirectoryDropHelper(directory)

            dragHelper = DragFilesHelper {
                selectedRows().map { it.file }
            }
        }

        override fun attached(resultsTab: ResultsTab, toolPane: ToolPane) {
            super.attached(resultsTab, toolPane)
            if (latestDirectory == directory) {
                resultsTab.isSelected = true
            }
        }

        override fun selected() {
            super.selected()
            // Sync the sidePanel's directory with the Results' directory
            directorySidePanel?.let {
                it.treeContent.tree.selectDirectory(directory)?.isExpanded = true
            }
            updateTitle()
        }

        override fun closed() {
            directoriesP.remove(directory)
            // Hitting "Back" will un-close the tab ;-)
            toolPane?.halfTab?.pushHistory()
        }
    }


    inner class DirectorySidePanel : SidePanel {

        private val tabPane = MyTabPane<MyTab>()

        override val node: Node = tabPane

        var treeContent = TreeTabContent()

        private val treeTab = MyTab("Tree", treeContent)

        private var placesContent = PlacesTabContent()

        private val placesTab = MyTab("Places")

        // Dropping a directory onto the "Tree" tab changes the root directory in the sideBars' tree.
        val treeRootDropHelper = DropFiles { _, files ->
            val newRoot = files.firstOrNull { it.isDirectory }
            if (newRoot != null && newRoot != treeContent.tree.rootDirectory) {
                treeContent.tree.rootDirectory = newRoot
                treeRootP.value = newRoot
                toolPane?.halfTab?.pushHistory()
            }
        }

        var foldSingleDirectories: Boolean
            get() = treeContent.tree.foldSingleDirectories
            set(v) {
                treeContent.tree.foldSingleDirectories = v
            }

        init {
            tabPane.side = Side.BOTTOM
            tabPane.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

            treeContent.tree.onSelected = { changeDirectory(it) }

            treeRootDropHelper.applyTo(treeTab)

            tabPane.add(treeTab)

            placesTab.content = placesContent
            tabPane.add(placesTab)

            // Expand the tree to the current viewed directory.
            directory?.let {
                treeContent.tree.selectDirectory(it)?.isExpanded = true
            }
        }

        fun update() {
            foldSingleDirectories = foldSingleDirectoriesP.value == true
            treeContent.tree.rootDirectory = treeRoot
            placesContent.file = placesFileP.value
        }
    }


    inner class TreeTabContent : BorderPane() {
        val tree = DirectoryTree(treeRoot, foldSingleDirectories = foldSingleDirectoriesP.value == true)

        init {
            center = tree
            val buttons = ToolBar()
            bottom = buttons

            buttons.items.add(ParataskActions.DIRECTORY_CHANGE_TREE_ROOT.createButton { focusOnParameter(treeRootP) })
        }

    }


    private inner class PlacesTabContent : BorderPane() {

        var placesListView: PlacesListView? = null

        var shortcuts = ShortcutHelper("Places Tab", this)

        var file: File? = null
            set(v) {
                val newValue = v ?: PlacesFile.defaultFile
                if (newValue != field) {
                    field = newValue
                    update()
                }
            }

        val buttons = ToolBar()

        init {
            file = placesFileP.value
            bottom = buttons
            placesListView?.onSelected = { changeDirectory(it) }
            update()
        }

        fun update() {
            buttons.items.clear()
            if (file?.exists() == true) {
                placesListView = PlacesListView(file!!)
                center = placesListView
                buttons.items.add(ParataskActions.DIRECTORY_EDIT_PLACES.createButton(shortcuts) { editPlaces() })
            } else {
                center = Label("")
            }
            buttons.items.add(ParataskActions.DIRECTORY_CHANGE_PLACES.createButton(shortcuts) { focusOnParameter(placesFileP) })
        }

        fun editPlaces() {
            val tab = toolPane?.halfTab?.projectTab
            val tool = PlacesTool()
            tool.filesP.value = listOf(file)
            tab?.projectTabs?.addAfter(tab, tool)
        }
    }

}


fun main(args: Array<String>) {
    TaskParser(DirectoryTool()).go(args)
}
