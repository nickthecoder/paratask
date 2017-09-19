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

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.DragFilesHelper
import uk.co.nickthecoder.paratask.misc.Thumbnailer
import uk.co.nickthecoder.paratask.misc.WrappedFile
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.project.Header
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.SizeColumn
import uk.co.nickthecoder.paratask.table.TableResults
import uk.co.nickthecoder.paratask.table.TimestampColumn
import uk.co.nickthecoder.paratask.table.filter.RowFilter
import uk.co.nickthecoder.paratask.tools.AbstractCommandTool
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.io.File

class LocateTool : AbstractCommandTool<WrappedFile>() {

    override val taskD = TaskDescription("locate", description = "Use 'mlocate' to find files matching a pattern")

    val patternsP = MultipleParameter("pattern", minItems = 1, value = listOf("")) {
        StringParameter("pattern", required = true)
    }

    val matchWholePathP = BooleanParameter("matchWholePath", value = true)

    val checkFileExitsP = BooleanParameter("checkFileExists", value = false)

    val regularExpressionP = BooleanParameter("regularExpressions", value = false, description = "Match using regular expressions? Otherwise use GLOBS.")

    val caseSensitiveP = BooleanParameter("caseSensitive", value = false)

    val maxItemsP = IntParameter("maxItems", required = false, value = null)

    val thumbnailer = Thumbnailer()

    override val rowFilter = RowFilter(this, columns, WrappedFile(File("")))


    init {
        taskD.addParameters(patternsP, matchWholePathP, checkFileExitsP, regularExpressionP, caseSensitiveP, maxItemsP)
        taskD.unnamedParameter = patternsP

        columns.add(Column<WrappedFile, ImageView>("icon", label = "", width = thumbnailer.heightP.value!! + 8, getter= {
            thumbnailer.thumbnailImageView(it.file)
        }))
        columns.add(Column<WrappedFile, String>("path", getter= { it.file.path }))
        columns.add(TimestampColumn<WrappedFile>("modified") { it.file.lastModified() })
        columns.add(SizeColumn<WrappedFile>("size") { it.file.length() })
    }

    override fun createHeader() = Header(this, patternsP)

    override fun createCommand(): OSCommand {
        val command = OSCommand("mlocate", "--quiet")

        if (matchWholePathP.value == false) {
            command.addArgument("--basename")
        }

        if (checkFileExitsP.value == true) {
            command.addArgument("--existing")
        }

        if (regularExpressionP.value == true) {
            command.addArgument("--regex")
        }

        if (caseSensitiveP.value == false) {
            command.addArgument("--ignore-case")
        }

        maxItemsP.value?.let {
            command.addArguments("--limit", it)
        }

        command.addArgument("--")
        patternsP.value.forEach { command.addArgument(it) }

        return command
    }

    override fun processLine(line: String) {
        list.add(WrappedFile(File(line)))
    }


    override fun createTableResults(): TableResults<WrappedFile> {
        val results = super.createTableResults()

        results.dragHelper = DragFilesHelper {
            results.selectedRows().map { it.file }
        }

        return results
    }

}
