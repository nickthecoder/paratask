package uk.co.nickthecoder.paratask.project

import javafx.scene.control.Tab


class ResultsTab(val results: Results) : Tab(results.label, results.node), MinorTab {
    init {
        this.textProperty().bind(results.labelProperty)
    }

    override fun selected() {
        results.selected()
    }

    override fun focus() {
        results.focus()
    }
}
