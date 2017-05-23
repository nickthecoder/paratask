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
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import uk.co.nickthecoder.paratask.options.OptionsRunner
import uk.co.nickthecoder.paratask.project.*
import uk.co.nickthecoder.paratask.util.uncamel

abstract class AbstractTool : Tool {

    override val taskRunner by lazy { ThreadedToolRunner(this) }

    override var toolPane: ToolPane? = null

    override val shortTitleProperty by lazy { SimpleStringProperty(defaultShortTitle()) }

    override var shortTitle: String
        get() = shortTitleProperty.get()
        set(value) {
            Platform.runLater { shortTitleProperty.set(value) }
        }

    override val optionsName: String by lazy { taskD.name }

    override val optionsRunner by lazy { OptionsRunner(this) }

    override var resultsList: List<Results> = listOf()

    protected fun defaultShortTitle() = taskD.name.uncamel()

    override fun attached(toolPane: ToolPane) {
        this.toolPane = toolPane
    }

    override fun detaching() {

        for (results in resultsList) {
            results.detaching()
        }
        this.toolPane = null
    }

    override fun check() {
        taskD.root.check()
        customCheck()
    }

    fun ensureToolPane(): ToolPane {
        return toolPane ?: ToolPane_Impl(this)
    }

    override fun customCheck() {}


    override val icon: Image? by lazy {
        ParaTaskApp.imageResource("tools/${iconName()}.png")
    }

    open fun iconName(): String = taskD.name

    override fun updateResults() {
        val newResults = createResults()
        toolPane?.replaceResults(newResults, resultsList)
        resultsList = newResults
    }

    protected fun singleResults(results: Results) = listOf(results)

    override fun toString(): String {
        return "Tool : $taskD"
    }
}