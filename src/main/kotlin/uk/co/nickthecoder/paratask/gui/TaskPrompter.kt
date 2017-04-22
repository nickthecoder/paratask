package uk.co.nickthecoder.paratask.gui

import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.Task

class TaskPrompter(val task: Task) {

    var root: BorderPane

    init {

        root = BorderPane()
        val ok = Button("OK")

        val form = Form()

        task.root.forEach() {
            val field: Field = it.createField()
            field.getStyleClass().add("field-${it.name}")
            form.addField(field)
        }

        root.getStyleClass().add("task-prompter")

        val scrollPane = ScrollPane(form)
        scrollPane.fitToWidthProperty().set(true)
        scrollPane.getStyleClass().add("scroll-pane")

        root.bottom = ok
        root.center = scrollPane

    }

    fun placeOnStage(stage: Stage) {
        stage.title = task.name

        val scene = Scene(root)

        val cssLocation = javaClass.getResource("paratask.css").toExternalForm()
        scene.getStylesheets().add(cssLocation)

        stage.setScene(scene)
        println("Showing TaskPrompter")
        stage.show()
    }
}