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

import uk.co.nickthecoder.paratask.tasks.FlipImageTask
import uk.co.nickthecoder.paratask.tasks.ResizeImageTask
import uk.co.nickthecoder.paratask.tasks.RotateImageTask
import uk.co.nickthecoder.paratask.tools.*
import uk.co.nickthecoder.paratask.tools.editor.EditorTool
import uk.co.nickthecoder.paratask.tools.git.*
import uk.co.nickthecoder.paratask.tools.places.*
import uk.co.nickthecoder.paratask.tools.terminal.GroovyTool
import uk.co.nickthecoder.paratask.tools.terminal.PythonTool
import uk.co.nickthecoder.paratask.tools.terminal.SSHTool
import uk.co.nickthecoder.paratask.tools.terminal.TerminalTool

/**
 * The core components of ParaTask
 */
class ParaTaskCore : Registers {

    override fun register() {

        TaskRegistry.home.addTasks(
                HomeTool(),
                DirectoryTool(), PlacesTool(), PlacesDirectoryTool(),
                FindTool(), LocateTool(), GrepTool(),
                TerminalTool(), SSHTool(), PythonTool(), GroovyTool(),
                WebTool(), EditorTool(),
                CustomToolListTool(),
                GitTool(), GitStatusTool(),
                OptionsFilesTool(), OptionsTool(),
                ShortcutsTool(),
                ProcessesTool(),
                MythRecordedTool()
        )

        TaskRegistry.topLevel.addTasks(TerminalTool())
        TaskRegistry.topLevel.addTasks(CommandTask())

        TaskRegistry.misc.addTasks(CodeHeaderTool())
        TaskRegistry.misc.addTasks(OpenProjectTask())

        val files = TaskGroup("Files")
        files.addTasks(DirectoryTool(), GrepTool(), FindTool(), LocateTool())
        files.addTasks(CopyFilesTask(), MoveFilesTask(), RenameFileTask(), GrepTask(), SearchAndReplaceTask(), CreateDirectoryTask())

        val git = TaskGroup("Git")
        git.addTasks(GitTool(), GitStatusTool(), GitLogTool(), GitStashTool(), GitCommittedFilesTool())
        git.addTasks(GitCommitTask(), GitRMTask())

        val images = TaskGroup("Images")
        images.addTasks(ImageViewerTool())
        images.addTasks(ResizeImageTask(), RotateImageTask(), FlipImageTask())

        TaskRegistry.addGroup(git)
        TaskRegistry.addGroup(files)
        TaskRegistry.addGroup(images)

        TaskRegistry.aliasTool(DirectoryTool(), "uk.co.nickthecoder.paratask.tools.places.DirectoryTreeTool")
        TaskRegistry.aliasTool(CustomToolListTool(), "uk.co.nickthecoder.paratask.tools.ListTool")
        TaskRegistry.aliasTool(TerminalTool(), "uk.co.nickthecoder.paratask.tools.TerminalTool")
        TaskRegistry.aliasTool(GroovyTool(), "uk.co.nickthecoder.paratask.tools.GroovyTool")
        TaskRegistry.aliasTool(PythonTool(), "uk.co.nickthecoder.paratask.tools.PythonTool")

        TaskRegistry.aliasTool(ImageViewerTool(), "uk.co.nickthecoder.paratask.tools.ImageTool")
        TaskRegistry.aliasTool(ImageViewerTool(), "uk.co.nickthecoder.paratask.tools.ImageViewTool")
    }

    companion object {
        val instance = ParaTaskCore()
    }
}
