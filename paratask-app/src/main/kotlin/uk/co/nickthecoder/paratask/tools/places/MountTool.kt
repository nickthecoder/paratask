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
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.TransferMode
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskParser
import uk.co.nickthecoder.paratask.ToolBarTool
import uk.co.nickthecoder.paratask.misc.FileOperations
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.project.MountPointButton
import uk.co.nickthecoder.paratask.project.ToolBarToolConnector
import uk.co.nickthecoder.paratask.project.ToolPane
import uk.co.nickthecoder.paratask.table.*
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import uk.co.nickthecoder.paratask.table.filter.SingleRowFilter
import uk.co.nickthecoder.paratask.util.Resource
import uk.co.nickthecoder.paratask.util.process.BufferedSink
import uk.co.nickthecoder.paratask.util.process.Exec
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.io.File
import java.util.regex.Pattern


class MountTool : ListTableTool<MountTool.MountPoint>(), SingleRowFilter<MountTool.MountPoint>, ToolBarTool {


    val toolBarSideP = ChoiceParameter<Side?>("toolbar", value = null, required = false)
            .nullableEnumChoices("None")
    override var toolBarSide by toolBarSideP

    val excludeFSTypesP = MultipleParameter("excludeFSTypes", value = listOf("tmpfs", "devtmpfs"), isBoxed = true) {
        StringParameter("excludeFSType")
    }

    val excludeMountPointsP = MultipleParameter("excludeMountPoints", isBoxed = true) {
        FileParameter("excludeMountPoint", mustExist = null, expectFile = null)
    }

    override val taskD = TaskDescription("mount", description = "Lists all mounted filesystems")
            .addParameters(toolBarSideP, excludeFSTypesP, excludeMountPointsP)


    private val exampleRow = MountPoint(File(""), "", "", false, 0, 0, 0)
    override val rowFilter = RowFilter(this, columns, exampleRow)

    override var toolBarConnector: ToolBarToolConnector? = null


    init {
        columns.add(Column<MountPoint, ImageView>("icon", label = "", getter = { ImageView(it.icon) }))
        columns.add(BooleanColumn<MountPoint>("mounted", label = "Mounted?", getter = { it.isMounted }))
        columns.add(Column<MountPoint, String>("label", getter = { it.label }))
        columns.add(Column<MountPoint, File>("path", getter = { it.file!! }))
        columns.add(Column<MountPoint, String>("type", getter = { it.fsType }))
        columns.add(SizeColumn<MountPoint>("size", getter = { it.size }))
        columns.add(SizeColumn<MountPoint>("used", getter = { it.used }))
        columns.add(SizeColumn<MountPoint>("available", getter = { it.available }))
    }

    override fun run() {
        val mounted = Lister(false).list()
        val mountedFiles = mounted.map { it.file }
        val maybeMounted = Lister(true).list()

        val tmpList = mutableListOf<MountPoint>()
        tmpList.addAll(mounted)
        tmpList.addAll(maybeMounted.filter { !mountedFiles.contains(it.file) })

        list.clear()
        list.addAll(tmpList.filter { accept(it) })
        list.sortBy { it.fsType }

        if (showingToolbar()) {
            Platform.runLater {
                updateToolbar(list.map { mountPoint -> MountPointButton(toolBarConnector!!.projectWindow, mountPoint) })
            }
        }

    }

    fun accept(mountPoint: MountPoint): Boolean {
        if (excludeFSTypesP.value.contains(mountPoint.fsType)) {
            return false
        }
        if (excludeMountPointsP.value.contains(mountPoint.file)) {
            return false
        }
        return true
    }

    override fun attached(toolPane: ToolPane) {
        super.attached(toolPane)
        if (toolBarConnector == null) {
            toolBarConnector = ToolBarToolConnector(toolPane.halfTab.projectTab.projectTabs.projectWindow, this, false)
        }
    }

    override fun createTableResults(): TableResults<MountPoint> {
        val tableResults = super.createTableResults()

        tableResults.dropHelper = object : TableDropFilesHelper<MountPoint>() {

            override fun acceptDropOnNonRow() = emptyArray<TransferMode>()

            override fun acceptDropOnRow(row: MountPoint) = if (row.isDirectory()) TransferMode.ANY else null

            override fun droppedOnRow(row: MountPoint, content: List<File>, transferMode: TransferMode) {
                if (row.isDirectory()) {
                    FileOperations.instance.fileOperation(content, row.file!!, transferMode)
                }
            }

            override fun droppedOnNonRow(content: List<File>, transferMode: TransferMode) {}
        }


        return tableResults
    }

    class Lister(val useFstab: Boolean) {

        val list = mutableListOf<MountPoint>()

        val columnPositions = Array(7) { Pair(0, 0) }

        fun list(): List<MountPoint> {
            val command = OSCommand("findmnt", "--bytes", "-P", "--df", "--evaluate")
            if (useFstab) {
                command.addArgument("--fstab")
            } else {
                command.addArgument("--mtab")
            }

            val exec = Exec(command)
            exec.outSink = BufferedSink { line ->
                processLine(line)
            }
            exec.start().waitFor()
            return list
        }

        val linePattern = Pattern.compile("""SOURCE="(.*)" FSTYPE="(.*)" SIZE="(.*)" USED="(.*)" AVAIL="(.*)" USE%="(.*)%" TARGET="(.*)"""")

        fun processLine(line: String) {
            val matcher = linePattern.matcher(line)

            if (matcher.matches()) {
                val target = matcher.group(7)

                if (target.startsWith('/')) {  // Exclude swap
                    list.add(MountPoint(
                            directory = File(target),
                            label = matcher.group(1),
                            fsType = matcher.group(2),
                            isMounted = !useFstab,
                            size = parseSize(matcher.group(3)),
                            used = parseSize(matcher.group(4)),
                            available = parseSize(matcher.group(5))))
                }
            }
        }

        fun parseSize(str: String): Long {
            try {
                return str.toLong()
            } catch (e: Exception) {
            }
            return 0
        }
    }

    class MountPoint(
            directory: File,
            label: String,
            val fsType: String,
            val isMounted: Boolean,
            val size: Long,
            val used: Long,
            val available: Long)

        : Place(Resource(directory), label) {

        val icon: Image? = ParaTask.imageResource("filetypes/${if (isMounted) "mounted" else "unmounted"}.png")
    }

}

fun main(args: Array<String>) {
    TaskParser(MountTool()).go(args)
}
