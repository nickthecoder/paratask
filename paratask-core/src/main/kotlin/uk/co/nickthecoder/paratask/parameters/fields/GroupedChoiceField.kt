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
import uk.co.nickthecoder.paratask.parameters.ParameterListener

class GroupedChoiceField<T>(val groupedChoiceParameter: GroupedChoiceParameter<T>)
    : ParameterField(groupedChoiceParameter), ParameterListener {

    val menuButton = MenuButton()

    init {
        groupedChoiceParameter.parameterListeners.add(this)
    }

    override fun parameterChanged(event: ParameterEvent) {
        if (event.type == ParameterEventType.VALUE) {
            menuButton.text = groupedChoiceParameter.getLabelForValue(groupedChoiceParameter.value)
        } else if (event.type == ParameterEventType.STRUCTURAL) {
            buildMenu()
        }
    }

    override fun createControl(): Node {
        buildMenu()
        return menuButton
    }

    private fun buildMenu() {
        menuButton.items.clear()
        groupedChoiceParameter.groups().toSortedMap().forEach { label, group ->

            val items: ObservableList<MenuItem>
            if (label.isNotBlank()) {
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

        menuButton.text = groupedChoiceParameter.getLabelForValue(groupedChoiceParameter.value)
    }

}
