package uk.co.nickthecoder.paratask.gui.project

import javafx.beans.property.SimpleStringProperty
import uk.co.nickthecoder.paratask.project.Tool

abstract class AbstractResults(
        override val tool: Tool,
        label: String = "Results")

    : Results {

    override val labelProperty = SimpleStringProperty()

    override var label: String
        get() = labelProperty.get()
        set(value) {
            labelProperty.set(value)
        }

    init {
        this.label = label
    }

    open override fun attached(toolPane: ToolPane) {}

    open override fun detaching() {}

    override fun selected() {
        node.requestFocus()
    }

    override fun deselected() {}

}