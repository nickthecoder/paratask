package uk.co.nickthecoder.paratask.project

import javafx.scene.layout.BorderPane

class HeadedResults(val results: Results, headerRows: HeaderRows) : Results {

    override val tool
        get() = results.tool

    override val label
        get() = results.label

    override val node = BorderPane()

    override val labelProperty
        get() = results.labelProperty

    init {
        node.center = results.node
        node.top = headerRows
    }

    override fun attached(resultsTab: ResultsTab, toolPane: ToolPane) {
        results.attached(resultsTab, toolPane)
    }

    override fun detaching() {
        results.detaching()
    }

    override fun selected() {
        results.selected()
    }

    override fun deselected() {
        results.deselected()
    }

    override fun focus() {
        results.focus()
    }

}
