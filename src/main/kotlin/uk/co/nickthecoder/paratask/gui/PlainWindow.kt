package uk.co.nickthecoder.paratask.gui

import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.util.AutoExit

class PlainWindow(val windowTitle: String, val root: Parent) : Stage() {

    init {
        val scene = Scene(root)
        ParaTaskApp.style(scene)

        setTitle(windowTitle)
        setScene(scene)
        sizeToScene()

        AutoExit.show(this)
    }
}