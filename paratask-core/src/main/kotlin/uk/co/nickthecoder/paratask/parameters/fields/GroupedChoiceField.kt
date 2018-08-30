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
package uk.co.nickthecoder.paratask.parameters.fields

import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Menu
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import uk.co.nickthecoder.paratask.parameters.GroupedChoiceParameter
import uk.co.nickthecoder.paratask.parameters.ParameterEvent
import uk.co.nickthecoder.paratask.parameters.ParameterEventType

class GroupedChoiceField<T>(val groupedChoiceParameter: GroupedChoiceParameter<T>)
    : ParameterField(groupedChoiceParameter) {

    val menuButton = MenuButton()

    init {
        groupedChoiceParameter.parameterListeners.add(this)
    }

    override fun parameterChanged(event: ParameterEvent) {
        super.parameterChanged(event)

        if (event.type == ParameterEventType.VALUE) {
            updateText()
        } else if (event.type == ParameterEventType.STRUCTURAL) {
            buildMenu()
        }
    }

    override fun createControl(): Node {
        buildMenu()
        return menuButton
    }

    private fun updateText() {
        menuButton.text = groupedChoiceParameter.getLabelForValue(groupedChoiceParameter.value) ?: " "
    }

    private fun buildMenu() {
        menuButton.items.clear()
        groupedChoiceParameter.groups().toSortedMap().forEach { label, group ->

            val items: ObservableList<MenuItem>
            if (label.isNotBlank() && groupedChoiceParameter.groups().size > 1 && (group.groupChoices.size > 1 || groupedChoiceParameter.allowSingleItemSubMenus)) {
                val subMenu = Menu(label)
                items = subMenu.items
                menuButton.items.add(subMenu)
            } else {
                items = menuButton.items
            }

            group.groupChoices.forEach { choice ->
                val menuItem = MenuItem(choice.label)
                menuItem.onAction = EventHandler { groupedChoiceParameter.value = choice.value }
                items.add(menuItem)
            }
            if (label.isBlank()) {
                items.add(SeparatorMenuItem())
            }
        }
        updateText()
    }

}
