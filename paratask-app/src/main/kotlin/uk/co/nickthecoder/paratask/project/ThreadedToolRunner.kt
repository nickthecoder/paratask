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

package uk.co.nickthecoder.paratask.project

import javafx.application.Platform
import uk.co.nickthecoder.paratask.ThreadedTaskRunner
import uk.co.nickthecoder.paratask.Tool

open class ThreadedToolRunner(val tool: Tool) : ThreadedTaskRunner(tool) {

    override fun runTask() {
        super.runTask()

        Platform.runLater {
            tool.updateResults()
            if (tool.toolPane?.halfTab?.projectTab?.isSelected == true) {
                // A runLater inside a runLater. WTF? Without this, the keyboard focus does not work correctly
                // Given that I've already had LOTS of trouble with TabPane not updating itself immediately, but
                // doing it fractionaly later, I am laying the blame there again. If the minor tab is not correctly
                // added to the scene graph at this time, then the focus may fail. Delaying using runLater bodges
                // the problem.
                Platform.runLater {
                    tool.toolPane?.focusResults()
                }
            }
        }
    }
}
