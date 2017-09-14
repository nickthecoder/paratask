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

import groovy.lang.Binding
import javafx.beans.property.ObjectProperty
import javafx.beans.property.StringProperty
import javafx.scene.image.Image
import uk.co.nickthecoder.paratask.gui.DropHelper
import uk.co.nickthecoder.paratask.options.GroovyScript
import uk.co.nickthecoder.paratask.options.Helper
import uk.co.nickthecoder.paratask.options.OptionsRunner
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.project.*

interface Tool : Task {

    var toolPane: ToolPane?

    val shortTitleProperty: StringProperty

    val shortTitle: String

    val longTitleProperty: StringProperty

    val longTitle: String

    val optionsName: String

    val optionsRunner: OptionsRunner

    var resultsList: List<Results>

    var tabDropHelper: DropHelper?

    val tabDropHelperProperty: ObjectProperty<DropHelper?>

    val projectWindow: ProjectWindow?
        get() = toolPane?.halfTab?.projectTab?.projectTabs?.projectWindow

    val project: Project?
        get() = toolPane?.halfTab?.projectTab?.projectTabs?.projectWindow?.project

    val hasSidePanel: Boolean
        get() = false

    /**
     * Note, this is separate from run because this must be done in JavaFX's thread, whereas run
     * will typically be done in its own thread.
     */
    fun createResults(): List<Results>

    fun getSidePanel(): SidePanel = throw IllegalStateException("Tool does not have a side panel")

    fun updateResults()

    fun attached(toolPane: ToolPane)

    fun detaching()

    fun createHeader(): HeaderOrFooter? = null

    fun createFooter(): HeaderOrFooter? = null

    override fun copy(): Tool = super.copy() as Tool

    /**
     * Copy this task, replacing any expressions with their evaluated values
     */
    fun evaluateTask(task: Task, row: Any?, rows: List<Any>?): Task {
        val copy = task.copy()

        for (parameter in copy.valueParameters()) {
            evaluateParameter(parameter, row = row, rows = rows)
        }

        return copy
    }

    private fun evaluateParameter(parameter: ValueParameter<*>, row: Any?, rows: List<Any>?) {

        if (parameter is MultipleParameter<*> && parameter.expression == null) {
            parameter.innerParameters.filter { it.expression != null }.forEach { innerParameter ->
                evaluateParameter(innerParameter, row = row, rows = rows)
            }
        } else {
            parameter.expression?.let { expression ->
                val gscript = GroovyScript(expression)
                val bindings = Binding()
                bindings.setProperty("tool", this)
                bindings.setProperty("row", row)
                bindings.setProperty("rows", rows)
                bindings.setProperty("helper", Helper.instance)

                parameter.evaluated(gscript.run(bindings))
                parameter.expression = null
            }
        }
    }

    companion object {
        fun create(creationString: String): Tool {
            return Task.create(creationString) as Tool
        }
    }
}
