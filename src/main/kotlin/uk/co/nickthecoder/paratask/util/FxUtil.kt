package uk.co.nickthecoder.paratask.util

import javafx.application.Platform
import javafx.event.Event
import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import java.util.concurrent.CountDownLatch

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

fun Node.requestFocusNext() {
    this.requestFocus()
    val tab = KeyEvent(null, null, KeyEvent.KEY_PRESSED, "", "\t", KeyCode.TAB, false, false, false, false)
    Event.fireEvent(this, tab)
}
