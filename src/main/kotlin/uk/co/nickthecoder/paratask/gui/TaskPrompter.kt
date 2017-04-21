package uk.co.nickthecoder.paratask.gui

import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ScrollPane
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.Task

class TaskPrompter(val stage: Stage, val task: Task) {

	init {
		stage.title = task.name

		val root = BorderPane()
		val ok = Button("OK")

		val form = Form()

		task.root.forEach() {
			form.addField(it.createField())
		}

		root.getStyleClass().add("task-prompter")

		val scrollPane = ScrollPane(form)
		scrollPane.fitToWidthProperty().set(true)
		scrollPane.getStyleClass().add("scroll-pane")

		root.bottom = ok
		root.center = scrollPane

		val scene = Scene(root)

		scene.getStylesheets().add(javaClass.getResource("paratask.css").toExternalForm())
		stage.setScene(scene)
		println("Showing TaskPrompter")
		stage.show()
	}

}