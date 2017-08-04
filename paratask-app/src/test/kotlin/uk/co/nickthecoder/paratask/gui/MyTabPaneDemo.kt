package uk.co.nickthecoder.paratask.gui

import javafx.application.Application
import javafx.geometry.Side
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.input.MouseEvent
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.ParaTaskApp


/**
 * A quick demo of basic usage
 */
class MyTabPaneDemo : Application() {

    override fun start(primaryStage: Stage) {
        primaryStage.setTitle("Hello World!")

        val tabPane = MyTabPane<MyTab>()

        val button1 = Button("Hello")
        val button2 = Button("World")
        button1.addEventHandler(MouseEvent.MOUSE_CLICKED) { tabPane.side = Side.TOP }
        button2.addEventHandler(MouseEvent.MOUSE_CLICKED) { tabPane.side = Side.BOTTOM }

        val helloTab = MyTab("Hello", button1)
        val worldTab = MyTab("World", button2)

        tabPane.add(helloTab)
        tabPane.add(worldTab)

        val scene = Scene(tabPane, 300.0, 250.0)
        ParaTask.style(scene)
        primaryStage.setScene(scene)
        primaryStage.show()
    }
}


fun main(args: Array<String>) {
    Application.launch(MyTabPaneDemo::class.java)
}

