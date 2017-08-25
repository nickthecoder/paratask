package uk.co.nickthecoder.paratask.project

import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.paratask.ParaTaskApp

class ResultsWithHeader(val results: Results, val headerRows: Header) : Results {

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
        ParaTaskApp.logFocus("ResultsWithHeader focus. results.focus()")
        results.focus()
    }

    override val canClose: Boolean
        get() = results.canClose

    override fun closed() {
        super.closed()
        results.closed()
    }

}
