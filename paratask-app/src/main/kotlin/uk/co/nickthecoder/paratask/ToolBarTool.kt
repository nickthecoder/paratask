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
