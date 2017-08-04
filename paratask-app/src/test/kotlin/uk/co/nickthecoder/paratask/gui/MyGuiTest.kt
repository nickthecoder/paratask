package uk.co.nickthecoder.paratask.gui

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Spinner
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import org.loadui.testfx.GuiTest
import java.util.concurrent.CountDownLatch

abstract class MyGuiTest : GuiTest() {

    /**
     * I was having intermittent failures, which I think was caused by the tests running before the scene was fully created.
     * This is my attempt at trying to fix the problem.
     */
    fun waitForScene(label: String) {
        for (i in 1..10) {
            try {
                if (find<Label>(label) != null) {
                    return
                }
            } catch (e: Exception) {
                println("Didn't find label : '${label}'. Sleeping")
            }
            sleep(1000)
        }
        println("*** Didn't find label : '${label}'. Gave up after many attempt.")
    }

    fun runAndWait(action: () -> Unit) {

        // run synchronously on JavaFX thread
        if (Platform.isFxApplicationThread()) {
            action()
            return
        }

        // queue on JavaFX thread and wait for completion
        val doneLatch = CountDownLatch(1)
        Platform.runLater {
            try {
                action()
            } finally {
                doneLatch.countDown()
            }
        }

        try {
            doneLatch.await()
        } catch (e: InterruptedException) {
            // ignore exception
        }
    }

    fun GuiTest.typeSequential(vararg keys: KeyCode): GuiTest {
        for (key in keys) {
            try {
                press(key)
            } finally {
                release(key)
            }
        }
        return this
    }

    fun GuiTest.clickAndClear(spinner: Spinner<*>): GuiTest {
        click(spinner)
        runAndWait { spinner.editor.text = "" }
        return this
    }

    fun GuiTest.clickAndClear(textField: TextField): GuiTest {
        click(textField)
        runAndWait { textField.text = "" }
        return this
    }

    fun findControl(parameterName: String): Node {
        return find<Node>(".control", find(".field-${parameterName}"))
    }

    fun findSpinner(parameterName: String): Spinner<*> {
        return findControl(parameterName) as Spinner<*>
    }

    fun findError(parameterName: String): Label {
        return find<Label>(".error", find(".field-${parameterName}"))
    }

    fun findTextField(parameterName: String): TextField {
        return findControl(parameterName) as TextField
    }


}