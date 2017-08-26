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
import javafx.scene.image.ImageView
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.gui.DragFilesHelper
import uk.co.nickthecoder.paratask.gui.DropFiles
import uk.co.nickthecoder.paratask.gui.MyTab
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.misc.*
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.project.*
import uk.co.nickthecoder.paratask.table.*
import uk.co.nickthecoder.paratask.util.*
import java.io.File

class DirectoryTool : AbstractTableTool<WrappedFile>(), HasDirectory {

    override val taskD = TaskDescription(name = "directory", description = "Work with a Single Directory")

    val directoriesP = MultipleParameter("directories", value = listOf(currentDirectory)) {
        FileParameter("dir", label = "Directory", expectFile = false, mustExist = true)
    }

    val treeRootP = FileParameter("treeRoot", required = false, expectFile = false)

    val filterGroupP = GroupParameter("filter")

    val onlyFilesP = BooleanParameter("onlyFiles", required = false, value = null)
    val extensionsP = MultipleParameter("extensions", label = "File Extensions") { StringParameter("") }
    val includeHiddenP = BooleanParameter("includeHidden", value = false)

    val thumbnailHeightP = IntParameter("thumbnailHeight", value = 32)

    val autoRefreshP = uk.co.nickthecoder.paratask.parameters.BooleanParameter("autoRefresh", value = true,
            description = "Refresh the list when the contents of the directory changes")

    val autoRefresh = AutoRefresh { toolPane?.parametersPane?.runIfNotAlreadyRunning() }

    val thumbnailer = Thumbnailer()


    val treeRoot: File
        get() = treeRootP.value ?: homeDirectory


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


    init {
        filterGroupP.addParameters(onlyFilesP, extensionsP, includeHiddenP)
        taskD.addParameters(directoriesP, treeRootP, filterGroupP, thumbnailHeightP, autoRefreshP)
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
            // The were for the rather naff "DirectoryTreeTool", which no longer exists.
        } else {
            super.loadProblem(parameterName, expression, stringValue)
        }
    }

    fun createColumns(): List<Column<WrappedFile, *>> {
        val columns = mutableListOf<Column<WrappedFile, *>>()

        columns.add(Column<WrappedFile, ImageView>("icon", label = "") { createImageView(it) })
        columns.add(FileNameColumn<WrappedFile>("name") { it.file })
        columns.add(ModifiedColumn<WrappedFile>("modified") { it.file.lastModified() })
        columns.add(SizeColumn<WrappedFile>("size") { it.file.length() })

        return columns
    }

    override fun run() {
        latestDirectory = selectedDirectoryTableResults()?.directory

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

    var sideBar: DirectorySideBar? = null

    fun createSideBar(): DirectorySideBar {
        return DirectorySideBar()
    }

    fun showSideBar() {
        if (sideBar == null) {
            sideBar = createSideBar()
        } else {
            sideBar?.update()
        }
        toolPane?.halfTab?.sideBar = sideBar
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        showSideBar()

        val button = toolPane.tabPane.createAddTabButton {
            onAddDirectory()
        }
        val dropHelper = DropFiles(arrayOf(TransferMode.COPY)) { _, files ->
            files.filter { it.isDirectory }.forEach {
                addDirectory(it)
            }
            true
        }
        dropHelper.applyTo(button)
    }

    override fun detaching() {
        super.detaching()
        autoRefresh.unwatchAll()
    }


    fun onAddDirectory() {
        val newFileP = directoriesP.addValue(null)

        toolPane?.parametersTab?.isSelected = true

        val field = toolPane?.parametersPane?.taskForm?.form?.findField(newFileP)
        Platform.runLater {
            ParaTaskApp.logFocus("AbstractDirectoryTool.onAddDirectory. field?.focusNext()")
            field?.focusNext()
        }
    }

    fun addDirectory(directory: File) {
        val innerP = directoriesP.addValue(directory) as FileParameter
        listDirectory(directory)
        val results = createResults(innerP)
        toolPane?.addResults(results)?.isSelected = true
        toolPane?.halfTab?.pushHistory()
    }

    fun createImageView(row: WrappedFile): ImageView {
        var result: ImageView? = null

        if (row.file.isImage()) {
            val thumbnail = thumbnailer.thumbnailImage(row.file)
            if (thumbnail != null) {
                result = ImageView()
                result.image = thumbnail
                result.fitHeight = thumbnailHeightP.value!!.toDouble()
                result.isPreserveRatio = true
                result.isSmooth = true
            }
        }

        if (result == null) {
            result = ImageView(row.icon)
        }

        return result
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

    /**
     * Called from the default option - either opens the file, or changes the directory of just the current results tab.
     * This is needed, because this tool allow for multiple results, and we only want to change directory of ONE.
     */
    fun open(file: File) {
        if (file.isDirectory) {
            selectedDirectoryTableResults()?.let {
                directoriesP.replace(it.directory, file)
                toolPane?.parametersPane?.run()
            }
        } else {
            ThreadedDesktop.instance.open(file)
        }
    }

    inner class DirectoryDropHelper(val directory: File) : TableDropFilesHelper<WrappedFile>() {

        override fun acceptDropOnRow(row: WrappedFile) = if (row.isDirectory()) TransferMode.ANY else null

        override fun acceptDropOnNonRow() = TransferMode.ANY

        override fun droppedOnRow(row: WrappedFile, content: List<File>, transferMode: TransferMode): Boolean {
            if (row.isDirectory()) {
                FileOperations.instance.fileOperation(content, row.file, transferMode)
                return true
            }
            return false
        }

        override fun droppedOnNonRow(content: List<File>, transferMode: TransferMode): Boolean {
            FileOperations.instance.fileOperation(content, directory, transferMode)
            return true
        }
    }

    inner class DirectoryTableResults(val directory: File, list: List<WrappedFile>)
        : TableResults<WrappedFile>(this@DirectoryTool, list, directory.name, createColumns(), canClose = true) {

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
            // Sync the sideBar's directory with the Results' directory
            sideBar?.let {
                it.directoryTree.selectDirectory(directory)?.isExpanded = true
            }
            updateTitle()
        }

        override fun closed() {
            directoriesP.remove(directory)
            // Hitting "Back" will un-close the tab ;-)
            toolPane?.halfTab?.pushHistory()
        }
    }

    inner class DirectorySideBar : MyTabPane<MyTab>() {

        var directoryTree = DirectoryTree(treeRoot)

        val treeTab = MyTab("Tree", directoryTree)

        // Dropping a directory onto the "Tree" tab changes the root directory in the sideBars' tree.
        val treeRootDropHelper = DropFiles() { event, files ->
            val newRoot = files.firstOrNull { it.isDirectory }
            if (newRoot != null && newRoot != directoryTree.rootDirectory) {
                directoryTree.rootDirectory = newRoot
                treeRootP.value = newRoot
                toolPane?.halfTab?.pushHistory()
            }
            true
        }

        init {
            side = Side.BOTTOM

            directoryTree.onSelected = { directory ->
                val oldDirectory = selectedDirectoryTableResults()?.directory
                if (oldDirectory != directory) {
                    directoriesP.replace(oldDirectory, directory)
                    toolPane?.parametersPane?.run()
                }
            }

            treeRootDropHelper.applyTo(treeTab)

            add(treeTab)
            // TODO Add a list of "Places"
        }

        fun update() {
            if (directoryTree.rootDirectory != treeRoot) {
                directoryTree = DirectoryTree(treeRoot)
                treeTab.content = directoryTree
            }
            // TODO Check if places file has changed.
        }
    }


}

fun main(args: Array<String>) {
    TaskParser(DirectoryTool()).go(args)
}
