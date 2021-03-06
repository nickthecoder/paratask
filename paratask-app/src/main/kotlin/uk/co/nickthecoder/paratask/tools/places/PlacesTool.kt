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
import javafx.scene.control.OverrunStyle
import javafx.scene.image.ImageView
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.*
import uk.co.nickthecoder.paratask.gui.*
import uk.co.nickthecoder.paratask.misc.AutoRefresh
import uk.co.nickthecoder.paratask.misc.FileOperations
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.nullableEnumChoices
import uk.co.nickthecoder.paratask.project.*
import uk.co.nickthecoder.paratask.table.*
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import uk.co.nickthecoder.paratask.table.filter.SingleRowFilter
import uk.co.nickthecoder.paratask.tools.NullTask
import uk.co.nickthecoder.paratask.util.Resource
import uk.co.nickthecoder.paratask.util.focusNext
import java.io.File

class PlacesTool : AbstractTableTool<PlaceInFile>(), SingleRowFilter<PlaceInFile>, ToolBarTool {

    override val taskD = TaskDescription("places", description = "Favourite Places")

    val filesP = MultipleParameter("files", value = listOf(PlacesFile.defaultFile)) {
        FileParameter("file", mustExist = null)
    }

    val placesFilesMap = mutableMapOf<File, PlacesFile>()

    val autoRefresh = AutoRefresh { toolPane?.parametersPane?.run() }

    // Used to select the correct ResultsTab when refreshing the tool
    var latestFile: PlacesFile? = null


    override val rowFilter = RowFilter(this, createColumns(), PlaceInFile(PlacesFile(File("")), Resource(File("")), ""))

    override var toolBarConnector: ToolBarToolConnector? = null

    val toolBarSideP = ChoiceParameter<Side?>("toolbar", value = null, required = false)
            .nullableEnumChoices("None", mixCase = true)
    override var toolBarSide by toolBarSideP


    init {
        taskD.addParameters(filesP, toolBarSideP)
    }

    fun createColumns(): List<Column<PlaceInFile, *>> {
        val columns = mutableListOf<Column<PlaceInFile, *>>()

        columns.add(Column<PlaceInFile, ImageView>("icon", label = "", getter = { ImageView(it.resource.icon) }))
        columns.add(Column<PlaceInFile, String>("label", getter = { it.label }))
        columns.add(TruncatedStringColumn<PlaceInFile>("name", width = 200, overrunStyle = OverrunStyle.CENTER_ELLIPSIS, getter = { it.name }))
        columns.add(Column<PlaceInFile, String>("location", getter = { it.resource.path }, filterGetter = { it.resource }))

        return columns
    }

    override fun loadProblem(parameterName: String, expression: String?, stringValue: String?) {
        // Backwards compatability. There used to be a single "file" parameter. Now its a MultipleParameter called "files".
        if (parameterName == "file") {
            filesP.clear()
            if (expression == null) {
                filesP.addValue(File(stringValue))
            } else {
                filesP.newValue().expression = expression
            }
            return
        }
        super<AbstractTableTool>.loadProblem(parameterName, expression, stringValue)
    }

    override fun createResults(): List<Results> {
        return filesP.innerParameters.filter { it.value != null }.map { fileP ->
            ResultsWithHeader(createResults(fileP.value!!), createHeader(fileP))
        }
    }

    fun createHeader(fileP: FileParameter): Header {
        return Header(this, fileP)
    }

    fun createResults(file: File): TableResults<PlaceInFile> {

        val placesFile = placesFilesMap[file]!!
        val tableResults = PlacesTableResults(placesFile)

        val filesDragHelper = DragFilesHelper {
            tableResults.selectedRows().filter { it.isFile() }.map { it.file!! }
        }

        val placesDragHelper = SimpleDragHelper<List<Place>>(Place.dataFormat, onMoved = { list ->
            list.forEach {
                placesFile.remove(it)
            }
            placesFile.save()
        }) {
            tableResults.selectedRows()
        }

        tableResults.dragHelper = CompoundDragHelper(placesDragHelper, filesDragHelper)

        val filesDropHelper: TableDropFilesHelper<Place> = object : TableDropFilesHelper<Place>() {

            override fun acceptDropOnNonRow() = arrayOf(TransferMode.LINK)

            override fun acceptDropOnRow(row: Place) = if (row.isDirectory()) TransferMode.ANY else null

            override fun droppedOnRow(row: Place, content: List<File>, transferMode: TransferMode) {
                if (row.isDirectory()) {
                    FileOperations.instance.fileOperation(content, row.file!!, transferMode)
                }
            }

            override fun droppedOnNonRow(content: List<File>, transferMode: TransferMode) {
                for (f in content) {
                    placesFile.places.add(PlaceInFile(placesFile, Resource(f), f.name))
                }
                placesFile.save()
            }

        }

        val placesDropHelper = SimpleDropHelper<List<Place>>(Place.dataFormat, arrayOf(TransferMode.COPY, TransferMode.MOVE)) { _, content ->

            content.forEach {
                placesFile.places.add(PlaceInFile(placesFile, it.resource, it.label))
            }
            placesFile.save()
        }

        tableResults.dropHelper = CompoundDropHelper(placesDropHelper, filesDropHelper)
        return tableResults
    }

    override fun run() {
        latestFile = selectedPlacesTableResults()?.placesFile

        filesP.value.filterNotNull().forEach { file ->
            val placesFile = PlacesFile(file)
            placesFilesMap[file] = placesFile
        }

        if (showingToolbar()) {
            Platform.runLater {
                val places = mutableListOf<Place>()
                placesFilesMap.values.forEach { places.addAll(it.places) }
                updateToolbar(places.map { place -> PlaceButton(toolBarConnector!!.projectWindow, place) })
            }
        }
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        toolPane.tabPane.createAddTabButton {
            onAddPlacesFile()
        }
        if (toolBarConnector == null) {
            toolBarConnector = ToolBarToolConnector(toolPane.halfTab.projectTab.projectTabs.projectWindow, this, true)
        }
    }

    override fun detaching() {
        autoRefresh.unwatchAll()
        super.detaching()
    }

    fun selectedPlacesTableResults(): PlacesTableResults? {
        val results = toolPane?.currentResults()
        if (results is ResultsWithHeader) {
            return results.results as PlacesTableResults
        }
        return null
    }

    fun onAddPlacesFile() {
        val fileP = filesP.addValue(null)

        toolPane?.parametersTab?.isSelected = true

        val field = toolPane?.parametersPane?.taskForm?.form?.findField(fileP)
        Platform.runLater {
            ParaTaskApp.logFocus("PlacesTool.onAddPlacesFile. field?.focusNext()")
            field?.control?.focusNext()
        }
    }

    fun taskNew(): Task {
        val results = selectedPlacesTableResults()
        return results?.placesFile?.taskNew() ?: NullTask()
    }

    inner class PlacesTableResults(val placesFile: PlacesFile) :
            TableResults<PlaceInFile>(this@PlacesTool, placesFile.places, placesFile.file.name, createColumns(), rowFilter = rowFilter, canClose = true) {

        init {
            autoRefresh.watch(placesFile.file)
        }

        override fun attached(resultsTab: ResultsTab, toolPane: ToolPane) {
            super.attached(resultsTab, toolPane)
            if (latestFile?.file == placesFile.file) {
                resultsTab.isSelected = true
            }
        }

        override fun selected() {
            super.selected()
            longTitle = "Places (${placesFile.file})"
        }

        override fun detaching() {
            super.detaching()
            autoRefresh.unwatch(placesFile.file)
        }


        override fun closed() {
            filesP.remove(placesFile.file)
            // Hitting "Back" will un-close the tab ;-)
            toolPane?.halfTab?.pushHistory()
        }
    }

}

fun main(args: Array<String>) {
    TaskParser(PlacesTool()).go(args)
}
