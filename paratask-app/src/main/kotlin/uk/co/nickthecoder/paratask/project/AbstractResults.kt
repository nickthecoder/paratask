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
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.Tool
import uk.co.nickthecoder.paratask.options.OptionsManager

abstract class AbstractResults(
        override val tool: Tool,
        label: String = "Results",
        override val canClose: Boolean = false)

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

    override fun attached(resultsTab: ResultsTab, toolPane: ToolPane) {
        // This must be a FILTER, otherwise the option edit field will consume the keystrokes before we get a go.
        node.addEventFilter(KeyEvent.KEY_PRESSED) { checkOptionShortcuts(it) }
    }

    /**
     * Checks the key event for all non-row options with a matching shortcut, and runs the option if found.
     * TableResults overrides this to handle row options.
     */
    open fun checkOptionShortcuts(event: KeyEvent) {

        val topLevelOptions = OptionsManager.getTopLevelOptions(tool.optionsName)
        topLevelOptions.listFileOptions().forEach { fileOptions ->
            fileOptions.listOptions().forEach { option ->

                option.shortcut?.let { shortcut ->
                    if (option.isRow == false) {
                        if (shortcut.match(event)) {
                            tool.optionsRunner.runNonRow(option)
                            event.consume()
                            return
                        }
                    }
                }
            }
        }
    }

    override fun detaching() {}

    override fun selected() {
        if (tool.toolPane?.halfTab?.projectTab?.isSelected == true) {
            ParaTaskApp.logFocus("AbstractResults.selected. focus()")
            focus()
        }
    }

    override fun deselected() {}

}
