package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.tools.editor.EditorTool
import uk.co.nickthecoder.paratask.tools.OSCommandTask
import uk.co.nickthecoder.paratask.tools.DirectoryTool
import uk.co.nickthecoder.paratask.tools.DirectoryTreeTool
import uk.co.nickthecoder.paratask.tools.GrepTool
import uk.co.nickthecoder.paratask.tools.GroovyTool
import uk.co.nickthecoder.paratask.tools.HomeTool
import uk.co.nickthecoder.paratask.tools.OptionsFilesTool
import uk.co.nickthecoder.paratask.tools.places.PlacesTool
import uk.co.nickthecoder.paratask.tools.PythonTool
import uk.co.nickthecoder.paratask.tools.TerminalTool
import uk.co.nickthecoder.paratask.tools.WebTool
import uk.co.nickthecoder.paratask.tools.git.*

object TaskRegistry {

    private val tasks = mutableListOf<Task>(
            OSCommandTask(), GitRMTask(), GitCommitTask())

    private val homeTools = mutableListOf<Tool>(
            HomeTool(),
            DirectoryTool(), DirectoryTreeTool(), PlacesTool(),
            TerminalTool(), PythonTool(), GroovyTool(),
            WebTool(), EditorTool(),
            GrepTool(), GitTool(),
            OptionsFilesTool())

    private val otherTools = mutableListOf<Tool>(
            GitLogTool(), GitCommittedFilesTool()
    )


    fun homeTools(): List<Tool> = homeTools.map { it.copy() }

    fun otherTools(): List<Tool> = otherTools.map { it.copy() }

    fun allTasks() = tasks.map { it.copy() }


    fun allTools() = homeTools() + otherTools()

    fun allTasksAndTools(): List<Task> = allTools() + allTasks()
}
