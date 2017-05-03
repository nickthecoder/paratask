package uk.co.nickthecoder.paratask.util

import javafx.application.Platform
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import uk.co.nickthecoder.paratask.gui.ParentBodge
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

fun Node.getParentBodge(): Parent? {
    return this.parent ?: if (this is ParentBodge) this.parentBodge() else null
}

fun getScene(node: Node): Scene? {

    fun tryHarder(): Scene? {
        var n: Node = node
        while (true) {
            val parent = n.getParentBodge()
            if (parent == null) {
                return n.getScene()
            }
            n = parent
        }
    }

    return node.getScene() ?: tryHarder()
}
