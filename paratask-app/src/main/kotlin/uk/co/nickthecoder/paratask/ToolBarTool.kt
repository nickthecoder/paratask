package uk.co.nickthecoder.paratask

import javafx.application.Platform
import javafx.geometry.Side
import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.project.ProjectWindow
import uk.co.nickthecoder.paratask.project.ToolBarToolConnector

interface ToolBarTool : Tool {

    var toolBarConnector: ToolBarToolConnector?

    var toolBarSide: Side?

    fun toolBarButtons(projectWindow: ProjectWindow): List<Button>

    fun updateToolbar() {

        Platform.runLater {
            toolBarConnector?.let { tc ->
                toolBarSide?.let {
                    tc.side = it
                }
                if (toolBarSide == null) {
                    tc.remove()
                } else {
                    tc.update()
                }
            }
        }
    }
}
