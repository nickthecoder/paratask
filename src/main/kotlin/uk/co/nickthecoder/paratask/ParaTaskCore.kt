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
package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.tools.*
import uk.co.nickthecoder.paratask.tools.editor.EditorTool
import uk.co.nickthecoder.paratask.tools.git.*
import uk.co.nickthecoder.paratask.tools.places.*
import java.io.File

/**
 * The core components of ParaTask
 */
class ParaTaskCore : Registers {

    internal constructor() {
    }

    override fun register() {

        val directoryP = FileParameter("directory", expectFile = false, required = true, value = File("").absoluteFile)
        TaskRegistry.projectData.addParameters(directoryP)

        TaskRegistry.home.addTools(
                HomeTool(), CustomToolListTool(),
                DirectoryTool(), DirectoryTreeTool(), PlacesTool(), PlacesDirectoryTool(),
                TerminalTool(), PythonTool(), GroovyTool(),
                WebTool(), EditorTool(),
                GrepTool(), GitTool(),
                OptionsFilesTool(), OptionsTool(),
                MythRecordedTool()
        )

        TaskRegistry.topLevel.addTools(TerminalTool())
        TaskRegistry.topLevel.addTasks(CommandTask())

        TaskRegistry.misc.addTools(CodeHeaderTool())

        val files = TaskGroup("Files")
        files.addTools(DirectoryTool(), DirectoryTreeTool(), GrepTool())
        files.addTasks(CopyFilesTask(), MoveFilesTask(), RenameFileTask(), GrepTask(), SearchAndReplaceTask())

        val git = TaskGroup("Git")
        git.addTools(GitTool(), GitLogTool(), GitCommittedFilesTool())
        git.addTasks(GitCommitTask(), GitRMTask())

        TaskRegistry.addGroup(git)
        TaskRegistry.addGroup(files)

        TaskRegistry.aliasTool(CustomToolListTool(), "uk.co.nickthecoder.paratask.tools.ListTool")
    }

    companion object {
        val instance = ParaTaskCore()
    }
}