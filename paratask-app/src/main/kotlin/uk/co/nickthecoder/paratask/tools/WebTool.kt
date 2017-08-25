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

package uk.co.nickthecoder.paratask.tools

import javafx.concurrent.Worker
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.project.AbstractResults
import uk.co.nickthecoder.paratask.project.Results
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.AbstractTool
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.project.Header
import uk.co.nickthecoder.paratask.project.HeaderRow

class WebTool() : AbstractTool() {

    constructor(address: String) : this() {
        addressP.value = address
    }

    override val taskD = TaskDescription("web", description = "A Simple Web Browser")

    val addressP = StringParameter("address")

    init {
        taskD.addParameters(addressP)
    }

    override fun createHeader() = Header(this, addressP)

    override fun run() {
    }

    override fun createResults(): List<Results> = singleResults(WebResults(this, addressP.value))

}

class WebResults(override val tool: WebTool, address: String) : AbstractResults(tool, "Web Page") {

    override val node = WebView()

    val webEngine: WebEngine = node.engine

    init {
        webEngine.load(address)

        webEngine.loadWorker.stateProperty().addListener { _, _, _ -> changedAddress(webEngine.location) }

        webEngine.loadWorker.stateProperty().addListener { _, _, newValue ->
            if (newValue == Worker.State.SUCCEEDED) {
                pageLoaded()
            }
        }
    }

    fun changedAddress(address: String) {
        tool.toolPane?.let { toolPane ->
            tool.addressP.value = address
            toolPane.halfTab.pushHistory()
        }
    }


    fun pageLoaded() {
        //val docTitle = node.getEngine().getTitle()
        // TODO Update the long title when implemented
    }

    override fun focus() {
        ParaTaskApp.logFocus("WebTool.focus. node.requestFocus()")
        node.requestFocus()
    }
}