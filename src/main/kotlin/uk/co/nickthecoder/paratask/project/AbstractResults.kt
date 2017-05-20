package uk.co.nickthecoder.paratask.project

import javafx.beans.property.SimpleStringProperty
import uk.co.nickthecoder.paratask.Tool

abstract class AbstractResults(
        override val tool:Tool,
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

    override fun attached(toolPane: ToolPane) {}

    override fun detaching() {}

    override fun selected() {
        node.requestFocus()
    }

    override fun deselected() {}
}
