package uk.co.nickthecoder.paratask

import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.project.ProjectWindow
import uk.co.nickthecoder.paratask.project.ToolBarToolConnector

interface ToolBarTool : Tool {
    var toolBarConnector: ToolBarToolConnector?
    fun toolBarButtons(projectWindow: ProjectWindow): List<Button>
}
