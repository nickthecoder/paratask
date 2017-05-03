package uk.co.nickthecoder.paratask.gui

import javafx.scene.Parent

/**
 * SplitPanes are broken - they do not update the parent of a node immediately.
 * This means that getScene() will incorrectly return null at times.
 * To get around this bug, the objects which are children of SplitPanes implement this interface, and
 * I can get around the problem.
 */
interface ParentBodge {
    fun parentBodge(): Parent?
}