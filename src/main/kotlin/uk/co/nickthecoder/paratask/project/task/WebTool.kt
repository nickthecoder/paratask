package uk.co.nickthecoder.paratask.project.task

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.concurrent.Worker
import javafx.concurrent.Worker.State
import javafx.scene.web.WebView
import uk.co.nickthecoder.paratask.SimpleTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.gui.project.EmptyResults
import uk.co.nickthecoder.paratask.parameter.StringParameter
import uk.co.nickthecoder.paratask.parameter.Values
import uk.co.nickthecoder.paratask.project.AbstractTool

class WebTool : AbstractTool() {

    override val taskD = TaskDescription("web", description="A Simple Web Browser")

    val addressP = StringParameter("address")

    private lateinit var address: String

    init {
        taskD.addParameters(addressP)
    }

    override fun run(values: Values) {
        address = addressP.value(values)
    }

    override fun updateResults() {
        val results = WebResults(this, address)

        toolPane?.updateResults(results)
    }
}

class WebResults(val webTool: WebTool, var address: String) : EmptyResults() {

    override val node = WebView()

    val webEngine = node.getEngine()

    init {
        webEngine.load(address);
        webEngine.getLoadWorker().stateProperty().addListener(object : ChangeListener<State> {

            override fun changed(observable: ObservableValue<out State>?, oldValue: State?, newValue: State?) {
                changedAddress(webEngine.getLocation())
            }
        })

        webEngine.getLoadWorker().stateProperty().addListener(object : ChangeListener<Worker.State> {
            override fun changed(
                    observable: ObservableValue <out Worker.State>?,
                    oldValue: Worker.State,
                    newValue: Worker.State) {

                if (newValue == Worker.State.SUCCEEDED) {
                    pageLoaded()
                }

            }
        });
    }

    fun changedAddress(address: String) {
        webTool.toolPane?.let { toolPane ->
            webTool.addressP.set(toolPane.values, address)
            toolPane.halfTab.pushHistory()
        }
    }


    fun pageLoaded() {
        val docTitle = node.getEngine().getTitle()
        // TODO Update the long title when implemented
    }
}