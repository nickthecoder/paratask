package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.tools.editor.EditorTool
import uk.co.nickthecoder.paratask.tools.OSCommandTask
import uk.co.nickthecoder.paratask.tools.DirectoryTool
import uk.co.nickthecoder.paratask.tools.DirectoryTreeTool
import uk.co.nickthecoder.paratask.tools.git.GitTool
import uk.co.nickthecoder.paratask.tools.GrepTool
import uk.co.nickthecoder.paratask.tools.GroovyTool
import uk.co.nickthecoder.paratask.tools.HomeTool
import uk.co.nickthecoder.paratask.tools.OptionsFilesTool
import uk.co.nickthecoder.paratask.tools.PlacesTool
import uk.co.nickthecoder.paratask.tools.PythonTool
import uk.co.nickthecoder.paratask.tools.TerminalTool
import uk.co.nickthecoder.paratask.tools.WebTool

object TaskRegistry {

    private val tasks = mutableListOf<Task>(
            OSCommandTask())

    private val tools = mutableListOf<Tool>(
            HomeTool(),
            DirectoryTool(), DirectoryTreeTool(), PlacesTool(),
            TerminalTool(), PythonTool(), GroovyTool(),
            WebTool(), EditorTool(),
            GrepTool(), GitTool(),
            OptionsFilesTool())

    fun allTasks() = tasks.map{ it.copy() }

    fun allTools() = tools.map{ it.copy() }

    fun allTasksAndTools() : List<Task> = allTasks() + allTools()
}
