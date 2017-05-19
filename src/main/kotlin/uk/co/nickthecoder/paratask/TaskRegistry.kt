package uk.co.nickthecoder.paratask

import uk.co.nickthecoder.paratask.project.Tool
import uk.co.nickthecoder.paratask.project.editor.EditorTool
import uk.co.nickthecoder.paratask.project.task.DirectoryTool
import uk.co.nickthecoder.paratask.project.task.DirectoryTreeTool
import uk.co.nickthecoder.paratask.project.task.GitTool
import uk.co.nickthecoder.paratask.project.task.GrepTool
import uk.co.nickthecoder.paratask.project.task.GroovyTool
import uk.co.nickthecoder.paratask.project.task.HomeTool
import uk.co.nickthecoder.paratask.project.task.OptionsFilesTool
import uk.co.nickthecoder.paratask.project.task.PlacesTool
import uk.co.nickthecoder.paratask.project.task.PythonTool
import uk.co.nickthecoder.paratask.project.task.TerminalTool
import uk.co.nickthecoder.paratask.project.task.WebTool

object TaskRegistry {

    val tasks = mutableListOf<CopyableTask>()

    val tools = mutableListOf<Tool>(
            HomeTool(),
            DirectoryTool(), DirectoryTreeTool(), PlacesTool(),
            TerminalTool(), PythonTool(), GroovyTool(),
            WebTool(), EditorTool(),
            GrepTool(), GitTool(),
            OptionsFilesTool())

}
