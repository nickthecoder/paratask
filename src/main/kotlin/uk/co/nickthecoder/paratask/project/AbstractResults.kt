package uk.co.nickthecoder.paratask.project

abstract class AbstractResults(
        override val tool: uk.co.nickthecoder.paratask.Tool,
        label: String = "Results")

    : Results {

    override val labelProperty = javafx.beans.property.SimpleStringProperty()

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