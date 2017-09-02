/*
ParaTask Copyright (C) 2017  Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package uk.co.nickthecoder.paratask.util

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.event.Event
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.util.Duration
import java.util.concurrent.CountDownLatch
import javafx.scene.control.ScrollBar


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

fun runAfterDelay(millis: Long, action: () -> Unit) {
    Timeline(KeyFrame(Duration.millis(millis.toDouble()), EventHandler({ action() }))).play()
}

fun Node.fireTabToFocusNext() {
    this.requestFocus()
    val tab = KeyEvent(null, null, KeyEvent.KEY_PRESSED, "", "\t", KeyCode.TAB, false, false, false, false)
    Event.fireEvent(this, tab)
}

fun Node.dumpAncestors() {
    var node: Node? = this
    while (node != null) {
        println(node)
        node = node.parent
    }
}

fun Node.findScrollbar(orientation: Orientation): ScrollBar? {
    for (n in lookupAll(".scroll-bar")) {
        if (n is ScrollBar) {
            if (n.orientation == orientation) {
                return n
            }
        }
    }
    return null
}
