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

import javafx.application.Application
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.project.ProjectWindow

// TODO Maybe ParaToolApp isn't needed, as we can make the Task the entry point
// Tasks can then have a parameter, or fixed value to determine if the results are to stdout,
// to a TextPane, or to a Tool window.

class ParaToolApp : Application() {
    override fun start(stage: Stage?) {
        if (stage == null) {
            return
        }
        val projectWindow = ProjectWindow()
        projectWindow.placeOnStage(stage)

        startTool?.let { tool ->
            projectWindow.addTool(tool)
        }
    }

    companion object {
        var startTool: Tool? = null
    }
}