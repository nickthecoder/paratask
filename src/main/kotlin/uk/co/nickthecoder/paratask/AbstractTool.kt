package uk.co.nickthecoder.paratask

import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.scene.image.Image
import uk.co.nickthecoder.paratask.options.OptionsRunner
import uk.co.nickthecoder.paratask.project.*
import uk.co.nickthecoder.paratask.util.uncamel

abstract class AbstractTool : Tool {

    override val taskRunner by lazy { ThreadedToolRunner(this) }

    override var toolPane: ToolPane? = null

    override val shortTitleProperty by lazy { SimpleStringProperty(defaultShortTitle()) }

    override var shortTitle: String
        get() = shortTitleProperty.get()
        set(value) {
            Platform.runLater { shortTitleProperty.set(value) }
        }

    override val optionsName: String by lazy { taskD.name }

    override var autoRun: Boolean = true

    override val optionsRunner by lazy{ OptionsRunner(this) }

    override var resultsList: List<Results> = listOf()

    protected fun defaultShortTitle() = taskD.name.uncamel()

    override fun attached(toolPane: ToolPane) {
        this.toolPane = toolPane

        if (autoRun) {
            toolPane.parametersPane.run()
        }
    }

    fun ensureToolPane(): ToolPane {
        return toolPane ?: ToolPane_Impl(this)
    }

    override fun check() {}

    override fun detaching() {

        for (results in resultsList) {
            results.detaching()
        }
        this.toolPane = null
    }

    override val icon: Image? by lazy {
        ParaTaskApp.imageResource("tools/${iconName()}.png")
    }

    open fun iconName(): String = taskD.name

    override fun updateResults() {
        val newResults = createResults()
        toolPane?.replaceResults(newResults, resultsList)
        resultsList = newResults
    }

    protected fun singleResults(results: Results) = listOf(results)

    override fun toString(): String {
        return "Tool : $taskD"
    }
}
