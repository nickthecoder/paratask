package uk.co.nickthecoder.paratask

import javafx.application.Platform
import javafx.geometry.Side
import javafx.scene.control.Button
import uk.co.nickthecoder.paratask.project.ToolBarToolConnector

interface ToolBarTool : Tool {

    var toolBarConnector: ToolBarToolConnector?

    var toolBarSide: Side?

    fun showingToolbar(): Boolean {
        if (toolBarSide == null) {
            Platform.runLater {
                toolBarConnector?.remove()
            }
            return false
        }
        return true
    }

    fun updateToolbar(buttons: List<Button>) {

        Platform.runLater {
            toolBarConnector?.let { tc ->
                toolBarSide?.let {
                    tc.side = it
                }
                if (toolBarSide == null) {
                    tc.remove()
                } else {
                    tc.update(buttons)
                }
            }
        }
    }
}
