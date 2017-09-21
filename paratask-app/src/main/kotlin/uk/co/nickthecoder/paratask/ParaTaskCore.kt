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
                DirectoryTool(), TrashTool(), PlacesTool(), PlaceListTool(), PlacesDirectoryTool(), MountTool(),
                FindTool(), LocateTool(), GrepTool(),
                TerminalTool(), SSHTool(), PythonTool(), GroovyTool(),
                WebTool(), EditorTool(),
                CustomTaskListTool(),
                GitTool(), GitStatusTool(),
                OptionsFilesTool(),
                ShortcutsTool(),
                ProcessesTool(),
                MythRecordedTool()
        )

        TaskRegistry.topLevel.addTasks(TerminalTool())
        TaskRegistry.topLevel.addTasks(CommandTask())

        val files = TaskGroup("Files")
        files.addTasks(DirectoryTool(), TrashTool(), GrepTool(), FindTool(), LocateTool())
        files.addTasks(CopyFilesTask(), MoveFilesTask(), RenameFileTask(), TrashTool.MoveToTrashTask(), TrashTool.RestoreFilesTask(), TrashTool.EmptyTrashTask())
        files.addTasks(GrepTask(), SearchAndReplaceTask(), CreateDirectoryTask())

        val places = TaskGroup("Places")
        places.addTasks(PlacesTool(), PlaceListTool(), PlacesDirectoryTool(), MountTool())

        val git = TaskGroup("Git")
        git.addTasks(GitTool(), GitStatusTool(), GitLogTool(), GitStashTool(), GitCommittedFilesTool())
        git.addTasks(GitCommitTask(), GitRMTask())

        val images = TaskGroup("Images")
        images.addTasks(ImageViewerTool())
        images.addTasks(ResizeImageTask(), RotateImageTask(), FlipImageTask())

        val paratask = TaskGroup("ParaTask")
        paratask.addTasks(OptionsTool(), OptionsFilesTool(), ShortcutsTool())
        paratask.addTasks(OpenProjectTask())

        val misc = TaskGroup("Miscellaneous")
        misc.addTasks(CodeHeaderTool(), MythRecordedTool(), ProcessesTool())

        TaskRegistry.addGroup(git)
        TaskRegistry.addGroup(files)
        TaskRegistry.addGroup(places)
        TaskRegistry.addGroup(images)
        TaskRegistry.addGroup(paratask)
        TaskRegistry.addGroup(misc)

        /*
         * Tasks/Tools that have been renamed. This allows saved objects to be restored correctly.
         */

        TaskFactory.addAlias("uk.co.nickthecoder.paratask.tools.CustomToolListTool", CustomTaskListTool())
        TaskFactory.addAlias("uk.co.nickthecoder.paratask.tools.ListTool", CustomTaskListTool())

        TaskFactory.addAlias("uk.co.nickthecoder.paratask.tools.places.DirectoryTreeTool", DirectoryTool())
        TaskFactory.addAlias("uk.co.nickthecoder.paratask.tools.TerminalTool", TerminalTool())
        TaskFactory.addAlias("uk.co.nickthecoder.paratask.tools.GroovyTool", GroovyTool())
        TaskFactory.addAlias("uk.co.nickthecoder.paratask.tools.PythonTool", PythonTool())

        TaskFactory.addAlias("uk.co.nickthecoder.paratask.tools.ImageTool", ImageViewerTool())
        TaskFactory.addAlias("uk.co.nickthecoder.paratask.tools.ImageViewTool", ImageViewerTool())
    }

    companion object {
        val instance = ParaTaskCore()
    }
}
