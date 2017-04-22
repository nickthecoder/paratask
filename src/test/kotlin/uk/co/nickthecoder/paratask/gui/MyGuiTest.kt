package uk.co.nickthecoder.paratask.gui

import javafx.scene.input.KeyCode
import org.loadui.testfx.GuiTest

abstract class MyGuiTest : GuiTest() {

    fun typeSequential(vararg keys: KeyCode): MyGuiTest {
        for( key in keys) {
            try {
                press(key)
            } finally {
                release(key)
            }
        }
        return this
    }
}