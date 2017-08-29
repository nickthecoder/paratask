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
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.misc.Thumbnailer
import uk.co.nickthecoder.paratask.misc.WrappedFile
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.project.Header
import uk.co.nickthecoder.paratask.project.HeaderRow
import uk.co.nickthecoder.paratask.table.BaseFileColumn
import uk.co.nickthecoder.paratask.table.Column
import uk.co.nickthecoder.paratask.table.ModifiedColumn
import uk.co.nickthecoder.paratask.table.SizeColumn
import uk.co.nickthecoder.paratask.tools.AbstractCommandTool
import uk.co.nickthecoder.paratask.util.HasDirectory
import uk.co.nickthecoder.paratask.util.process.OSCommand
import java.io.File

class FindTool : AbstractCommandTool<WrappedFile>(), HasDirectory {

    enum class MatchType { GLOB_CASE_SENSITIVE, GLOB_CASE_INSENSITIVE, REGEX_CASE_SENSITIVE, REGEX_CASE_INSENSITIVE }

    override val taskD = TaskDescription("find")

    val directoryP = FileParameter("directory", expectFile = false, mustExist = true)

    val nameGroupP = GroupParameter("matchName")
    val filenameP = StringParameter("filename", required = false)
    val matchTypeP = ChoiceParameter("matchType", value = MatchType.GLOB_CASE_INSENSITIVE).enumChoices(true)

    val wholeNameP = BooleanParameter("wholeName", value = false)

    val emptyFilesP = BooleanParameter("emptyFiles", value = false)

    val traverseGroupP = GroupParameter("traverseOptions")
    val followSymlinksP = BooleanParameter("followSymlinks", value = false)
    val otherFileSystemsP = BooleanParameter("otherFileSystems", value = true)
    val minDepthP = IntParameter("minDepth", required = false)
    val maxDepthP = IntParameter("maxDepth", required = false)

    val filterGroupP = GroupParameter("filter")
    val userP = StringParameter("user", required = false)
    val groupP = StringParameter("group", required = false)
    val newerThanFileP = FileParameter("newerThanFile", required = false, mustExist = true)
    val typeP = ChoiceParameter<String?>("type", value = null, required = false)
    val xtypeP = BooleanParameter("typeFollowsSymlink", value = true)


    val thumbnailHeightP = IntParameter("thumbnailHeight", value = 32)

    val thumbnailer = Thumbnailer()

    override val directory by directoryP


    init {
        typeP.addChoice("", null, "Any")
        typeP.addChoice("f", "f", "Regular File")
        typeP.addChoice("d", "d", "Directory")
        typeP.addChoice("l", "l", "Symbolic Link")
        typeP.addChoice("s", "s", "Socket Link")
        typeP.addChoice("p", "p", "Named Pipe")
        typeP.addChoice("b", "b", "Special Block File")
        typeP.addChoice("c", "c", "Special Character File")

        nameGroupP.addParameters(filenameP, matchTypeP, wholeNameP)
        traverseGroupP.addParameters(followSymlinksP, otherFileSystemsP, minDepthP, maxDepthP)
        filterGroupP.addParameters(userP, groupP, typeP, xtypeP, emptyFilesP, newerThanFileP)
        taskD.addParameters(directoryP, nameGroupP, filterGroupP, traverseGroupP, thumbnailHeightP)

        matchTypeP.listen {
            if (matchTypeP.value == MatchType.REGEX_CASE_SENSITIVE || matchTypeP.value == MatchType.REGEX_CASE_INSENSITIVE) {
                wholeNameP.value = true
            }
        }
    }

    override fun createColumns(): List<Column<WrappedFile, *>> {

        val columns = mutableListOf<Column<WrappedFile, *>>()

        columns.add(Column<WrappedFile, ImageView>("icon", label = "") { it.createImageView(thumbnailer, thumbnailHeightP.value!!) })
        columns.add(BaseFileColumn<WrappedFile>("file", base = directory!!) { it.file })
        columns.add(ModifiedColumn<WrappedFile>("modified") { it.file.lastModified() })
        columns.add(SizeColumn<WrappedFile>("size") { it.file.length() })

        return columns
    }

    override fun customCheck() {
        if (wholeNameP.value != true && (matchTypeP.value == MatchType.REGEX_CASE_SENSITIVE || matchTypeP.value == MatchType.GLOB_CASE_SENSITIVE)) {
            throw(ParameterException(matchTypeP, "Can only use regex for whole-name matching"))
        }
    }

    override fun createCommand(): OSCommand {

        val command = OSCommand("find")
        command.directory = directory!!

        if (followSymlinksP.value == true) {
            command.addArgument("-L")
        }
        minDepthP.value?.let {
            command.addArgument("-mindepth")
            command.addArgument(it)
        }
        maxDepthP.value?.let {
            command.addArguments("-maxdepth", it)
        }
        if (otherFileSystemsP.value == false) {
            command.addArgument("-mount")
        }

        if (emptyFilesP.value == true) {
            command.addArgument("")
        }

        if (filenameP.value.isNotBlank()) {
            if (wholeNameP.value == true) {

                when (matchTypeP.value) {
                    MatchType.GLOB_CASE_INSENSITIVE -> command.addArguments("-iwholename", filenameP.value)
                    MatchType.GLOB_CASE_SENSITIVE -> command.addArguments("-wholename", filenameP.value)
                    MatchType.REGEX_CASE_SENSITIVE -> command.addArguments("-regex", filenameP.value)
                    MatchType.REGEX_CASE_INSENSITIVE -> command.addArguments("-iregex", filenameP.value)
                }

            } else {
                when (matchTypeP.value) {
                    MatchType.GLOB_CASE_INSENSITIVE -> command.addArguments("-iname", filenameP.value)
                    MatchType.GLOB_CASE_SENSITIVE -> command.addArguments("-name", filenameP.value)
                    MatchType.REGEX_CASE_SENSITIVE -> throw(ParameterException(matchTypeP, "Can only use regex for whole-name matching"))
                    MatchType.REGEX_CASE_INSENSITIVE -> throw(ParameterException(matchTypeP, "Can only use regex for whole-name matching"))
                }
            }
        }

        typeP.value?.let {
            if (xtypeP.value == true) {
                command.addArguments("-xtype", it)
            } else {
                command.addArguments("-type", it)
            }
        }

        return command
    }

    override fun createHeader(): Header? {
        return Header(this, HeaderRow(directoryP), HeaderRow(filenameP, matchTypeP, typeP))
    }

    override fun processLine(line: String) {
        val l2 = if (line.startsWith("./")) line.substring(2) else line
        val file = directory!!.resolve(File(l2))

        list.add(WrappedFile(file))
    }

}
