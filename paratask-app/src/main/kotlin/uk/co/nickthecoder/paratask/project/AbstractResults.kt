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

import javafx.beans.property.SimpleStringProperty
import javafx.scene.input.KeyEvent
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.options.OptionsManager

abstract class AbstractResults(
        override val tool: Tool,
        label: String = "Results")

    : Results {

    override val labelProperty = SimpleStringProperty()

    final override var label: String
        get() = labelProperty.get()
        set(value) {
            labelProperty.set(value)
        }

    init {
        this.label = label
    }

    override fun attached(toolPane: ToolPane) {
        node.addEventHandler(KeyEvent.KEY_PRESSED) { event ->
            val topLevelOptions = OptionsManager.getTopLevelOptions(tool.optionsName)
            topLevelOptions.listFileOptions().forEach { fileOptions ->
                fileOptions.listOptions().forEach{ option ->
                    if (option.isRow == false) {
                        option.shortcut?.let {
                            if ( it.match(event) ) {
                                event.consume()
                                tool.optionsRunner.runNonRow( option )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun detaching() {}

    override fun selected() {
        focus()
    }

    override fun deselected() {}
}
