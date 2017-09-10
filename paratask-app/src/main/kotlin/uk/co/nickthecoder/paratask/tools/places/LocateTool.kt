package uk.co.nickthecoder.paratask.tools.places

import javafx.scene.image.ImageView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.DragFilesHelper
import uk.co.nickthecoder.paratask.gui.DragHelper
import uk.co.nickthecoder.paratask.misc.Thumbnailer
import uk.co.nickthecoder.paratask.misc.WrappedFile
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.project.Header
import uk.co.nickthecoder.paratask.table.*
import uk.co.nickthecoder.paratask.tools.AbstractCommandTool
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.io.File

class LocateTool : AbstractCommandTool<WrappedFile>() {

    override val taskD = TaskDescription("locate", description = "Use 'mlocate' to find files matching a pattern")

    val patternsP = MultipleParameter<String>("pattern", minItems = 1, value = listOf("")) {
        StringParameter("pattern", required = true)
    }

    val matchWholePathP = BooleanParameter("matchWholePath", value = true)

    val checkFileExitsP = BooleanParameter("checkFileExists", value = false)

    val regularExpressionP = BooleanParameter("regularExpressions", value = false, description = "Match using regular expressions? Otherwise use GLOBS.")

    val caseSensitiveP = BooleanParameter("caseSensitive", value = false)

    val maxItemsP = IntParameter("maxItems", required = false, value = null)

    val thumbnailer = Thumbnailer()

    override val rowFilter = RowFilter<WrappedFile>(this, columns, WrappedFile(File("")))


    init {
        taskD.addParameters(patternsP, matchWholePathP, checkFileExitsP, regularExpressionP, caseSensitiveP, maxItemsP)
        taskD.unnamedParameter = patternsP

        columns.add(Column<WrappedFile, ImageView>("icon", label = "", width = thumbnailer.heightP.value!! + 8) { thumbnailer.thumbnailImageView(it.file) })
        columns.add(Column<WrappedFile, String>("path") { it.file.path })
        columns.add(ModifiedColumn<WrappedFile>("modified") { it.file.lastModified() })
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
