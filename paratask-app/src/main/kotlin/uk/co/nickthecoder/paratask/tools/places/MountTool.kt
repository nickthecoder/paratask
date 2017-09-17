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
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.ToolBarTool
import uk.co.nickthecoder.paratask.misc.FileOperations
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.nullableEnumChoices
import uk.co.nickthecoder.paratask.project.PlaceButton
import uk.co.nickthecoder.paratask.project.ToolBarToolConnector
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.table.*
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import uk.co.nickthecoder.paratask.table.filter.SingleRowFilter
import uk.co.nickthecoder.paratask.util.Resource
import java.io.File
import java.lang.reflect.Field
import java.nio.file.FileStore
import java.nio.file.FileSystems
import java.nio.file.Path


class MountTool : ListTableTool<MountTool.MountPoint>(), SingleRowFilter<MountTool.MountPoint>, ToolBarTool {

    override val taskD = TaskDescription("mount", description = "Lists all mounted filesystems")


    override val rowFilter = RowFilter(this, columns, MountPoint(File(""), null))

    override var toolBarConnector: ToolBarToolConnector? = null

    val toolBarSideP = ChoiceParameter<Side?>("toolbar", value = null, required = false)
            .nullableEnumChoices("None")
    override var toolBarSide by toolBarSideP


    init {
        taskD.addParameters(toolBarSideP)

        columns.add(Column<MountPoint, ImageView>("icon", label = "", getter = { ImageView(it.resource.icon) }))
        columns.add(Column<MountPoint, String>("label", getter = { it.label }))
        columns.add(Column<MountPoint, File>("path", getter = { it.file!! }))
        columns.add(SizeColumn<MountPoint>("size", getter = { it.size }))
        columns.add(SizeColumn<MountPoint>("used", getter = { it.used }))
        columns.add(SizeColumn<MountPoint>("available", getter = { it.available }))
    }

    override fun run() {
        val stores = FileSystems.getDefault().fileStores
        list.clear()

        stores.forEach { fileStore ->
            val file = createFile(fileStore)
            if (file != null) {
                val mountPoint = MountPoint(file, fileStore)
                if (mountPoint.accept()) {
                    list.add(mountPoint)
                }
            }
        }

        if (showingToolbar()) {
            Platform.runLater {
                updateToolbar(list.map { row -> PlaceButton(toolBarConnector!!.projectWindow, row) })
            }
        }
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        if (toolBarConnector == null) {
            toolBarConnector = ToolBarToolConnector(toolPane.halfTab.projectTab.projectTabs.projectWindow, this, false)
        }
    }

    override fun createTableResults(): TableResults<MountPoint> {
        val tableResults = super.createTableResults()

        tableResults.dropHelper = object : TableDropFilesHelper<Place>() {

            override fun acceptDropOnNonRow() = emptyArray<TransferMode>()

            override fun acceptDropOnRow(row: Place) = if (row.isDirectory()) TransferMode.ANY else null

            override fun droppedOnRow(row: Place, content: List<File>, transferMode: TransferMode) {
                if (row.isDirectory()) {
                    FileOperations.instance.fileOperation(content, row.file!!, transferMode)
                }
            }

            override fun droppedOnNonRow(content: List<File>, transferMode: TransferMode) {}
        }


        return tableResults
    }


    class MountPoint(file: File, val fileStore: FileStore?) : Place(Resource(file), fileStore?.name() ?: "") {
        var size: Long = 0
        var used: Long = 0
        var available: Long = 0

        init {
            try {
                size = fileStore?.totalSpace ?: 0
                available = fileStore?.unallocatedSpace ?: 0
                used = size - available
            } catch (e: Exception) {
                // Do nothing
            }
        }

        fun accept(): Boolean {

            if (size <= 0) {
                return false
            }
            with(fileStore) {
                if (this == null) {
                    return false
                }
                if (name() == "tmpfs") {
                    return false
                }
                if (name() == "udev") {
                    return false
                }
                if (name() == "rootfs") {
                    return false
                }
            }
            return true
        }
    }

    companion object {

        fun createFile(store: FileStore): File? {
            // Look for a private field called "file" or "root" in the FileStore. (Only works for Unix type systems).
            try {
                var klass: Class<*>? = store.javaClass
                var field: Field? = null
                do {
                    try {
                        field = klass!!.getDeclaredField("file")
                    } catch (e: Exception) {
                        // Do nothing
                    }

                    if (field != null) {
                        break
                    }
                    klass = klass!!.superclass
                } while (klass != null)

                // If we found the field, great, get the path.
                if (field != null) {
                    field.isAccessible = true
                    return (field.get(store) as Path).toFile()
                }

            } catch (e: Exception) {
                // Couldn't find the actual mount point.
                // e.printStackTrace();
            }

            return null
        }


    }
}

fun main(args: Array<String>) {
    TaskParser(MountTool()).go(args)
}
