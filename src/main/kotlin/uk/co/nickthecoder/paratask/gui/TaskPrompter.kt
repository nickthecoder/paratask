package uk.co.nickthecoder.paratask.gui

import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParameterException
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameter.Values

open class TaskPrompter(val task: Task, val values: Values) {

    var root: BorderPane

    var stage: Stage? = null

    val okButton: Button

    val cancelButton: Button

    val applyButton: Button

    init {

        root = BorderPane()

        okButton = Button("OK")
        okButton.onAction = EventHandler {
            run()
        }
        okButton.defaultButtonProperty().set(true)

        cancelButton = Button("Cancel")
        cancelButton.onAction = EventHandler { cancel() }
        cancelButton.visibleProperty().set(false)
        cancelButton.cancelButtonProperty().set(true)

        applyButton = Button("Apply")
        applyButton.onAction = EventHandler { apply() }
        //applyButton.visibleProperty().set(false)

        val buttons = FlowPane()
        buttons.styleClass.add("buttons")
        buttons.children.add(okButton)
        buttons.children.add(cancelButton)
        buttons.children.add(applyButton)

        val form = Form()

        task.taskD.root.forEach() {
            val field: Field = it.createField(values)
            field.getStyleClass().add("field-${it.name}")
            form.addField(field)
        }

        root.getStyleClass().add("task-prompter")

        val scrollPane = ScrollPane(form)
        scrollPane.fitToWidthProperty().set(true)
        scrollPane.getStyleClass().add("scroll-pane")

        root.bottom = buttons
        root.center = scrollPane

    }

    open protected fun cancel() {
        close()
    }

    open protected fun close() {
        stage?.let { it.hide() }
    }

    open protected fun run() {
        apply()
        close()
    }

    open protected fun apply() {
        try {
            // TODO Copy the values
            task.check(values)
        } catch (e: ParameterException) {
            // TODO Highlight the error
        }
        task.run(values)
    }

    fun placeOnStage(stage: Stage) {
        this.stage = stage
        stage.title = task.taskD.name

        cancelButton.visibleProperty().set(true)

        val scene = Scene(root)

        val cssLocation = javaClass.getResource("paratask.css").toExternalForm()
        scene.getStylesheets().add(cssLocation)

        stage.setScene(scene)
        println("Showing TaskPrompter")
        stage.show()
    }
}